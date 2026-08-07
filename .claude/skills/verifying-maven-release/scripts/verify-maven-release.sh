#!/usr/bin/env bash
#
# verify-maven-release.sh — Validate a Maven repository release (gh-pages style).
#
# Two scopes of checks:
#   REPO-WIDE (always, strong integrity guarantees, cheap):
#     2. HASHES        — every .md5/.sha1/.sha256/.sha512 sidecar matches a freshly
#                        computed hash of its target file.
#     3. METADATA      — each module's maven-metadata.xml <versions> list matches the
#                        version directories on disk (no missing/extra/duplicate).
#     5. POM RESOLVE   — every inter-module com.salesforce.marketingcloud dependency
#                        in every POM resolves to a version directory in the repo.
#
#   RELEASE-SCOPED (only the version dirs this release adds — see --base):
#     1. NO SOURCE     — release dirs contain only .aar/.pom/.html + hash sidecars;
#                        AARs contain compiled classes.jar and no .java/.kt; no
#                        -sources.jar / -javadoc.jar.
#     4. INDEX FILES   — per-version index.html links every artifact in its dir; the
#                        module index.html links each newly added version.
#
# Why scoped: the repo retains legacy artifacts (old javadoc jars, plugin .jars,
# older index.html conventions) that are NOT part of a new release. Auditing those
# is a separate concern; this script verifies that THIS release is well-formed.
#
# Usage:
#   verify-maven-release.sh [REPO_ROOT] [--base <git-ref>] [--all] [VERSION_DIR ...]
#
#   REPO_ROOT      Path to the 'repository' dir (default: ./repository).
#   --base REF     Release = version dirs ADDED vs <REF> (e.g. gh-pages). RECOMMENDED.
#   --all          Release-scoped checks run over EVERY version dir (full audit;
#                  will surface legacy issues). Mutually exclusive with --base.
#   VERSION_DIR    One or more explicit version dirs to treat as the release,
#                  e.g. com/salesforce/marketingcloud/sfmcsdk/3.1.1
#
#   If neither --base, --all, nor explicit dirs are given, defaults to --base gh-pages.
#
# Exit code: 0 = all checks passed, 1 = one or more failures.

set -uo pipefail

REPO_ROOT="repository"
BASE_REF=""
ALL=0
EXPLICIT_DIRS=()
while [ $# -gt 0 ]; do
  case "$1" in
    --base) BASE_REF="${2:-}"; shift 2;;
    --all)  ALL=1; shift;;
    -h|--help) grep '^#' "$0" | sed 's/^# \{0,1\}//'; exit 0;;
    --*) echo "unknown option: $1" >&2; exit 2;;
    */*) EXPLICIT_DIRS+=("$1"); shift;;          # looks like a path → explicit version dir
    *)   REPO_ROOT="$1"; shift;;
  esac
done

[ -d "$REPO_ROOT" ] || { echo "ERROR: repo root '$REPO_ROOT' not found" >&2; exit 1; }
REPO_ROOT="${REPO_ROOT%/}"

# default scope
if [ "$ALL" -eq 0 ] && [ -z "$BASE_REF" ] && [ "${#EXPLICIT_DIRS[@]}" -eq 0 ]; then
  BASE_REF="gh-pages"
fi

FAIL=0
note_fail() { FAIL=1; echo "  FAIL: $*"; }

hash_md5() { if command -v md5sum >/dev/null 2>&1; then md5sum "$1" | awk '{print $1}'; else md5 -q "$1"; fi; }
hash_sha() { local bits="$1" f="$2"
             if command -v shasum >/dev/null 2>&1; then shasum -a "$bits" "$f" | awk '{print $1}';
             else "sha${bits}sum" "$f" | awk '{print $1}'; fi; }

# ------------------------------------------------------------
# Determine RELEASE_DIRS (absolute paths to version directories)
# ------------------------------------------------------------
RELEASE_DIRS=()
is_version_dir() { ls "$1"/*.aar "$1"/*.pom >/dev/null 2>&1; }

if [ "${#EXPLICIT_DIRS[@]}" -gt 0 ]; then
  for d in "${EXPLICIT_DIRS[@]}"; do
    full="$REPO_ROOT/$d"; [ -d "$d" ] && full="$d"
    [ -d "$full" ] && RELEASE_DIRS+=("$full") || echo "  WARNING: dir not found: $d" >&2
  done
elif [ "$ALL" -eq 1 ]; then
  while IFS= read -r d; do is_version_dir "$d" && RELEASE_DIRS+=("$d"); done \
    < <(find "$REPO_ROOT" -mindepth 1 -type d)
elif [ -n "$BASE_REF" ]; then
  if git rev-parse --verify "$BASE_REF" >/dev/null 2>&1; then
    while IFS= read -r vd; do
      [ -n "$vd" ] && [ -d "$vd" ] && RELEASE_DIRS+=("$vd")
    done < <(git diff --name-status "$BASE_REF"...HEAD -- "$REPO_ROOT" 2>/dev/null \
              | awk '$1=="A"{print $2}' \
              | sed -E 's#/[^/]+$##' | sort -u)
  else
    echo "ERROR: base ref '$BASE_REF' not found in git" >&2; exit 1
  fi
fi

echo "Repo root : $REPO_ROOT"
if [ -n "$BASE_REF" ]; then echo "Scope     : added vs '$BASE_REF'"
elif [ "$ALL" -eq 1 ]; then echo "Scope     : ALL version dirs (full audit)"
else echo "Scope     : explicit dirs"; fi
echo "Release dirs (${#RELEASE_DIRS[@]}):"
for d in "${RELEASE_DIRS[@]}"; do echo "    ${d#"$REPO_ROOT"/}"; done
[ "${#RELEASE_DIRS[@]}" -eq 0 ] && echo "    (none — nothing release-scoped to check)"
echo

# ============================================================
# 1. NO SOURCE FILES  (release-scoped)
# ============================================================
echo "== [1/5] No source files in release dirs =="
ALLOWED='\.(aar|pom|html|md5|sha1|sha256|sha512)$'
for vdir in "${RELEASE_DIRS[@]}"; do
  for f in "$vdir"/*; do
    fn=$(basename "$f")
    echo "$fn" | grep -qE "$ALLOWED" || echo "$fn" | grep -q '^maven-metadata.xml' \
      || note_fail "${vdir#"$REPO_ROOT"/}/$fn — unexpected file type"
  done
  # AAR internals
  if command -v unzip >/dev/null 2>&1; then
    for aar in "$vdir"/*.aar; do
      [ -e "$aar" ] || continue
      listing=$(unzip -l "$aar" 2>/dev/null)
      echo "$listing" | grep -q 'classes.jar' || note_fail "$(basename "$aar"): no classes.jar"
      echo "$listing" | grep -qE '\.(java|kt)$' && note_fail "$(basename "$aar"): contains source files"
    done
  fi
done
[ "$FAIL" -eq 0 ] && echo "  OK"

# ============================================================
# 2. HASHES  (repo-wide)
# ============================================================
echo "== [2/5] Hash sidecars (repo-wide) =="
pass=0; hfail=0
while IFS= read -r base; do
  for alg in md5 sha1 sha256 sha512; do
    hf="$base.$alg"; [ -f "$hf" ] || continue
    case "$alg" in
      md5)    calc=$(hash_md5 "$base");;
      sha1)   calc=$(hash_sha 1   "$base");;
      sha256) calc=$(hash_sha 256 "$base");;
      sha512) calc=$(hash_sha 512 "$base");;
    esac
    stored=$(tr -d '[:space:]' < "$hf")
    if [ "$calc" = "$stored" ]; then pass=$((pass+1));
    else hfail=$((hfail+1)); note_fail "$hf"; echo "      stored=$stored calc=$calc"; fi
  done
done < <(find "$REPO_ROOT" -type f \( -name '*.aar' -o -name '*.pom' -o -name 'maven-metadata.xml' \))
echo "  $pass matched, $hfail mismatched"
[ "$hfail" -gt 0 ] && FAIL=1

# ============================================================
# 3. METADATA vs DIRS  (repo-wide)
# ============================================================
echo "== [3/5] maven-metadata.xml vs version directories (repo-wide) =="
m_issues=0
while IFS= read -r meta; do
  mdir=$(dirname "$meta"); module=$(basename "$mdir")
  dirs=$(find "$mdir" -mindepth 1 -maxdepth 1 -type d -exec basename {} \; | sort -u)
  vers_block=$(awk '/<versions>/{f=1} /<\/versions>/{f=0} f' "$meta" \
               | grep -oE '<version>[^<]+</version>' | sed -E 's#</?version>##g')
  listed=$(echo "$vers_block" | sort -u)
  dups=$(echo "$vers_block" | sort | uniq -d)
  [ -n "$dups" ] && { note_fail "$module: duplicate version(s) in metadata: $(echo $dups)"; m_issues=1; }
  miss=$(comm -23 <(echo "$dirs") <(echo "$listed"))
  extra=$(comm -13 <(echo "$dirs") <(echo "$listed"))
  [ -n "$miss" ]  && { note_fail "$module: dir(s) not in metadata: $(echo $miss)"; m_issues=1; }
  [ -n "$extra" ] && { note_fail "$module: metadata version(s) with no dir: $(echo $extra)"; m_issues=1; }
done < <(find "$REPO_ROOT" -type f -name 'maven-metadata.xml')
[ "$m_issues" -eq 0 ] && echo "  OK"

# ============================================================
# 4. INDEX FILES  (release-scoped)
# ============================================================
echo "== [4/5] index.html link integrity (release dirs) =="
i_issues=0
for vdir in "${RELEASE_DIRS[@]}"; do
  idx="$vdir/index.html"
  if [ ! -f "$idx" ]; then note_fail "${vdir#"$REPO_ROOT"/}: per-version index.html missing"; i_issues=1; continue; fi
  for f in "$vdir"/*; do
    fn=$(basename "$f"); [ "$fn" = "index.html" ] && continue
    grep -qF "href=\"$fn\"" "$idx" || { note_fail "${vdir#"$REPO_ROOT"/}/index.html missing link to $fn"; i_issues=1; }
  done
  # module index must link this version
  v=$(basename "$vdir"); midx="$(dirname "$vdir")/index.html"
  if [ -f "$midx" ]; then
    grep -qE "href=\"$v/?\"" "$midx" || { note_fail "$(dirname "${vdir#"$REPO_ROOT"/}")/index.html missing link to $v"; i_issues=1; }
  else
    note_fail "$(dirname "${vdir#"$REPO_ROOT"/}")/index.html (module index) missing"; i_issues=1
  fi
done
[ "$i_issues" -eq 0 ] && echo "  OK"

# ============================================================
# 5. POM INTER-MODULE DEPENDENCY RESOLUTION  (repo-wide)
# ============================================================
echo "== [5/5] POM com.salesforce.marketingcloud dependency resolution (repo-wide) =="
GROUP_DIR=$(find "$REPO_ROOT" -type d -name marketingcloud -path '*com/salesforce/*' | head -1)
p_issues=0
while IFS= read -r pom; do
  while read -r dep ver; do
    [ -z "${dep:-}" ] && continue
    if [ -n "$GROUP_DIR" ] && [ ! -d "$GROUP_DIR/$dep/$ver" ]; then
      note_fail "${pom#"$REPO_ROOT"/} -> unresolved dep $dep:$ver"; p_issues=1
    fi
  done < <(awk '
      /<groupId>com.salesforce.marketingcloud<\/groupId>/ {g=1; next}
      g && /<artifactId>/ {sub(/.*<artifactId>/,""); sub(/<\/artifactId>.*/,""); a=$0; next}
      g && /<version>/    {sub(/.*<version>/,"");    sub(/<\/version>.*/,"");    print a" "$0; g=0}
    ' "$pom")
done < <(find "$REPO_ROOT" -type f -name '*.pom')
[ "$p_issues" -eq 0 ] && echo "  OK"

echo
if [ "$FAIL" -eq 0 ]; then
  echo "RESULT: PASS — release artifacts are valid."
  exit 0
else
  echo "RESULT: FAIL — see issues above."
  exit 1
fi
