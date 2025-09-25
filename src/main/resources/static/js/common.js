// Управление темой
function toggleTheme() {
    document.body.classList.toggle('dark-theme');
    const isDark = document.body.classList.contains('dark-theme');
    localStorage.setItem('theme', isDark ? 'dark' : 'light');
    const themeText = document.getElementById('themeText');
    if (themeText) {
        themeText.textContent = isDark ? 'Светлая тема' : 'Тёмная тема';
    }
}

// Загрузка темы из localStorage
function loadTheme() {
    const savedTheme = localStorage.getItem('theme') || 'light';
    if (savedTheme === 'dark') {
        document.body.classList.add('dark-theme');
        const themeText = document.getElementById('themeText');
        if (themeText) {
            themeText.textContent = 'Светлая тема';
        }
    }
}

// Инициализация при загрузке страницы
document.addEventListener('DOMContentLoaded', function() {
    loadTheme();
});