package com.example.model

import androidx.compose.ui.graphics.Color

enum class AppThemeMode(val displayName: String) {
    SYSTEM("System Default"),
    DARK("Dark Theme (OLED / Cyber)"),
    LIGHT("Light Theme (Clean Crisp)")
}

enum class SyntaxTheme(
    val displayName: String,
    val isDark: Boolean,
    val background: Color,
    val surface: Color,
    val text: Color,
    val keyword: Color,
    val string: Color,
    val comment: Color,
    val number: Color,
    val function: Color,
    val type: Color,
    val decorator: Color,
    val lineNumber: Color
) {
    MONOKAI_PRO(
        displayName = "Monokai Pro",
        isDark = true,
        background = Color(0xFF2D2A2E),
        surface = Color(0xFF221F22),
        text = Color(0xFFFCFCFA),
        keyword = Color(0xFFFF6188),
        string = Color(0xFFFFD866),
        comment = Color(0xFF727072),
        number = Color(0xFFAB9DF2),
        function = Color(0xFFA9DC76),
        type = Color(0xFF78DCE8),
        decorator = Color(0xFFFC9867),
        lineNumber = Color(0xFF5B595C)
    ),
    ONE_DARK_PRO(
        displayName = "One Dark Pro",
        isDark = true,
        background = Color(0xFF282C34),
        surface = Color(0xFF21252B),
        text = Color(0xFFABB2BF),
        keyword = Color(0xFFC678DD),
        string = Color(0xFF98C379),
        comment = Color(0xFF5C6370),
        number = Color(0xFFD19A66),
        function = Color(0xFF61AFEF),
        type = Color(0xFFE5C07B),
        decorator = Color(0xFFE06C75),
        lineNumber = Color(0xFF4B5263)
    ),
    CYBERPUNK_NEON(
        displayName = "Cyberpunk Neon",
        isDark = true,
        background = Color(0xFF0F0E17),
        surface = Color(0xFF1E1E2E),
        text = Color(0xFFFFFFFE),
        keyword = Color(0xFFFF0055),
        string = Color(0xFF00F0FF),
        comment = Color(0xFF6272A4),
        number = Color(0xFFFEE715),
        function = Color(0xFF00FFA3),
        type = Color(0xFFFF80BF),
        decorator = Color(0xFFBD93F9),
        lineNumber = Color(0xFF4A4B6E)
    ),
    DRACULA(
        displayName = "Dracula",
        isDark = true,
        background = Color(0xFF282A36),
        surface = Color(0xFF21222C),
        text = Color(0xFFF8F8F2),
        keyword = Color(0xFFFF79C6),
        string = Color(0xFFF1FA8C),
        comment = Color(0xFF6272A4),
        number = Color(0xFFBD93F9),
        function = Color(0xFF50FA7B),
        type = Color(0xFF8BE9FD),
        decorator = Color(0xFFFFB86C),
        lineNumber = Color(0xFF6272A4)
    ),
    GITHUB_DARK(
        displayName = "GitHub Dark",
        isDark = true,
        background = Color(0xFF0D1117),
        surface = Color(0xFF161B22),
        text = Color(0xFFC9D1D9),
        keyword = Color(0xFFFF7B72),
        string = Color(0xFFA5D6FF),
        comment = Color(0xFF8B949E),
        number = Color(0xFF79C0FF),
        function = Color(0xFFD2A8FF),
        type = Color(0xFFFFA657),
        decorator = Color(0xFF7EE787),
        lineNumber = Color(0xFF484F58)
    ),
    GITHUB_LIGHT(
        displayName = "GitHub Light",
        isDark = false,
        background = Color(0xFFFFFFFF),
        surface = Color(0xFFF6F8FA),
        text = Color(0xFF24292F),
        keyword = Color(0xFFCF222E),
        string = Color(0xFF0A3069),
        comment = Color(0xFF6E7781),
        number = Color(0xFF0550AE),
        function = Color(0xFF8250DF),
        type = Color(0xFF953800),
        decorator = Color(0xFF116329),
        lineNumber = Color(0xFF8C959F)
    ),
    SOLARIZED_DARK(
        displayName = "Solarized Dark",
        isDark = true,
        background = Color(0xFF002B36),
        surface = Color(0xFF073642),
        text = Color(0xFF839496),
        keyword = Color(0xFF859900),
        string = Color(0xFF2AA198),
        comment = Color(0xFF586E75),
        number = Color(0xFFD33682),
        function = Color(0xFF268BD2),
        type = Color(0xFFB58900),
        decorator = Color(0xFFCB4B16),
        lineNumber = Color(0xFF586E75)
    )
}
