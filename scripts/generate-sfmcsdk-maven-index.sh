#!/bin/bash
mkdir -p repository/com/salesforce/marketingcloud/sfmcsdk
for DIR in $(find ./repository/com/salesforce/marketingcloud/sfmcsdk -type d); do
  (
    echo -e "<html>\n<body>\n<h1>Directory listing</h1>\n<hr/>\n<pre>"
    ls -1pa "${DIR}" | grep -v "^\./$" | grep -v "^index\.html$" | grep -v "^\.DS\_Store$" | awk '{ printf "<a href=\"%s\">%s</a>\n",$1,$1 }'
    echo -e "</pre>\n</body>\n</html>"
  ) > "${DIR}/index.html"
done