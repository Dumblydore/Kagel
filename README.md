# Kagel
Kagel is a library and Gradle plugin for Android development that generates Android Views from Composable functions. 
With Kagel, developers can annotate their `@Composable` functions with `@BindView` annotation and the plugin will automatically generate a Custom View wrapper around the Composable.
This makes it easier to integrate Composable-based UI code with existing Android View-based code.

## Usage
**Currently Kagel is only compatible with ksp.**

Add the Kagel Gradle plugin to your Android project by including the following in your project-level build.gradle file:
```
buildscript {
    dependencies {
        // ...
        classpath "TODO"
    }
}
```

Replace <latest-version> with the latest version of the Kagel plugin.

Apply the Kagel plugin in your app-level build.gradle file:
`apply plugin: 'kagel'`

Define your composable theme by using the `@BindTheme` annotation. For example:
```
@BindTheme
@DefaultTheme
fun MyTheme(content: @Composable () -> Unit) { //@BindTheme composables must have only one argument!
    //...
}
```

Annotate your Composable functions with the `@BindView` annotation. For example:
```
@BindView
@Composable
fun MyComposable() {
    // Implementation of Custom View
}
```


## Customization
`@BindView` has man 
```
@BindView(
    name = "<custom_view_name>", //default is <composable_name>View,
    theme = "<custom_theme_name" //default is the Theme that's marked as @DefaultTheme 
)
```

## TODO
- [ ] Stub out api
- [ ] Implement code generation from @BindView
- [ ] Implement code generation for @BindTheme
- [ ] Write tests
- [ ] Build sample app
