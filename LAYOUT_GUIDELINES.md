# Layout Guidelines for Marketing Cloud SDK Android App

## Overview

This document outlines the standardized layout patterns and styles to ensure consistent spacing,
padding, and visual design across all screens in the app.

## Global Layout System

### 1. Scrollable Screen Layout

For any screen that needs scrollable content, use the standardized styles:

```xml

<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto" style="@style/Widget.App.ScrollView">

    <LinearLayout style="@style/Widget.App.ContentContainer">

        <!-- Your content here -->

    </LinearLayout>
</ScrollView>
```

### 2. Last Card Styling

For the last card/section in any scrollable layout, use:

```xml

<com.google.android.material.card.MaterialCardView android:layout_width="match_parent"
    android:layout_height="wrap_content" style="@style/Widget.App.LastCard">

    <!-- Card content -->

</com.google.android.material.card.MaterialCardView>
```

## Available Styles

### Layout Styles

- `@style/Widget.App.ScrollView` - Consistent scrollable container
- `@style/Widget.App.ContentContainer` - Main content container with proper padding
- `@style/Widget.App.LastCard` - Last card with extra bottom margin

### Dimension Resources

- `@dimen/screen_padding` - Standard screen padding (16dp)
- `@dimen/screen_bottom_padding` - Bottom padding for scrollable content (200dp)
- `@dimen/card_bottom_margin` - Standard card bottom margin (16dp)
- `@dimen/last_card_bottom_margin` - Last card bottom margin (80dp)
- `@dimen/card_elevation` - Standard card elevation (4dp)
- `@dimen/card_corner_radius` - Standard card corner radius (16dp)
- `@dimen/button_top_margin` - Button top margin (16dp)
- `@dimen/button_bottom_margin` - Button bottom margin (48dp)

## Benefits

### ✅ **Consistency**

- All screens have identical spacing and padding
- No more manual adjustments for each screen
- Professional, cohesive user experience

### ✅ **Maintainability**

- Change spacing globally by updating dimension resources
- No need to hunt through individual layout files
- Easy to update design system in the future

### ✅ **Developer Experience**

- No more back-and-forth on padding adjustments
- Clear guidelines for new screens
- Reduced layout-related bugs

## Usage Examples

### Creating a New Screen

1. Start with the base scrollable layout structure
2. Add your content cards inside the `LinearLayout`
3. Apply `@style/Widget.App.LastCard` to the last card
4. Use dimension resources for consistent spacing

### Updating Existing Screens

1. Replace manual ScrollView attributes with `style="@style/Widget.App.ScrollView"`
2. Replace manual LinearLayout attributes with `style="@style/Widget.App.ContentContainer"`
3. Update the last card to use `style="@style/Widget.App.LastCard"`

## Advanced Usage

### Base Fragment Class (Optional)

For complex screens, consider extending `BaseScrollableFragment` which provides:

- Automatic scrollable layout setup
- Helper methods for consistent styling
- Programmatic card creation with proper styling

### Include Layout (Alternative)

Use `@layout/include_scrollable_container` for a template-based approach:

```xml

<include layout="@layout/include_scrollable_container" />
```

## Migration Status

✅ **Completed Screens:**

- Home Screen (`fragment_home.xml`)
- Registration Screen (`fragment_registration.xml`)
- View Registration Screen (`fragment_view_registration.xml`)

## Future Considerations

- Consider creating styles for other common UI patterns (buttons, input fields, etc.)
- Add tablet-specific dimension resources for responsive design
- Create animation styles for consistent transitions

