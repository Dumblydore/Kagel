package me.mauricee.kagel

/**
 * Annotation that indicates the associated Composable function should be wrapped in a Custom View.
 * The Custom View is generated by the Kagel plugin based on the Composable function and its parameters.
 * This annotation is used to facilitate the generation of Custom Views that can be easily integrated
 * with existing Android View-based code.
 *
 * Example usage:
 *
 * ```
 * @BindView
 * @Composable
 * fun MyComposable() {
 *     // Implementation of Composable
 * }
 * ```
 *
 * The `BindView` annotation must be used in conjunction with the Kagel plugin to generate the Custom View.
 * The plugin will detect the annotated Composable function and generate the appropriate Custom View code.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
annotation class BindView
