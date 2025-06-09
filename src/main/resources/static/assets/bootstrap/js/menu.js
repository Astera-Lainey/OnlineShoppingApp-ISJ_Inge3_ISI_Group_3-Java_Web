// Menu functionality for the shopping application
(function() {
    // Check if jQuery is available, otherwise use vanilla JavaScript
    var $ = window.jQuery || window.$;
    
    function initializeMenu() {
        // Mobile menu toggle
        var mobileMenu = document.querySelector('.mobile-menu');
        var mainMenu = document.querySelector('.main-menu');
        
        if (mobileMenu && mainMenu) {
            mobileMenu.addEventListener('click', function() {
                mainMenu.classList.toggle('active');
            });
        }

        // Search functionality
        var searchBarIcon = document.querySelector('.search-bar-icon');
        var searchArea = document.querySelector('.search-area');
        var closeBtn = document.querySelector('.close-btn');
        
        if (searchBarIcon && searchArea) {
            searchBarIcon.addEventListener('click', function() {
                searchArea.classList.add('active');
            });
        }
        
        if (closeBtn && searchArea) {
            closeBtn.addEventListener('click', function() {
                searchArea.classList.remove('active');
            });
        }

        // Dropdown menu functionality
        var dropdownToggles = document.querySelectorAll('.dropdown-toggle');
        dropdownToggles.forEach(function(toggle) {
            toggle.addEventListener('click', function(e) {
                e.preventDefault();
                var dropdownMenu = this.nextElementSibling;
                if (dropdownMenu && dropdownMenu.classList.contains('dropdown-menu')) {
                    dropdownMenu.classList.toggle('show');
                }
            });
        });

        // Close dropdown when clicking outside
        document.addEventListener('click', function(e) {
            if (!e.target.closest('.dropdown')) {
                var dropdownMenus = document.querySelectorAll('.dropdown-menu');
                dropdownMenus.forEach(function(menu) {
                    menu.classList.remove('show');
                });
            }
        });

        // Sticky header
        var sticker = document.getElementById('sticker');
        if (sticker) {
            window.addEventListener('scroll', function() {
                if (window.scrollY > 100) {
                    sticker.classList.add('sticky');
                } else {
                    sticker.classList.remove('sticky');
                }
            });
        }

        // Smooth scrolling for anchor links
        var anchorLinks = document.querySelectorAll('a[href^="#"]');
        anchorLinks.forEach(function(link) {
            link.addEventListener('click', function(e) {
                e.preventDefault();
                var targetId = this.getAttribute('href');
                var target = document.querySelector(targetId);
                if (target) {
                    target.scrollIntoView({
                        behavior: 'smooth',
                        block: 'start'
                    });
                }
            });
        });
    }

    // Initialize when DOM is ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initializeMenu);
    } else {
        initializeMenu();
    }
})(); 