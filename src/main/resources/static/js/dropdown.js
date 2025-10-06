// 드롭다운 토글 함수
function toggleDropdown(event) {
    event.preventDefault();
    event.stopPropagation();
    
    const dropdown = document.getElementById('categoryDropdown');
    const toggle = dropdown.querySelector('.dropdown-toggle');
    const isActive = dropdown.classList.contains('active');
    
    // 토글
    dropdown.classList.toggle('active');
    
    // 화살표 변경
    if (isActive) {
        toggle.innerHTML = '상품 ▼';
    } else {
        toggle.innerHTML = '상품 ▲';
    }
}

// 다른 곳 클릭 시 드롭다운 닫기
document.addEventListener('click', function(event) {
    const dropdown = document.getElementById('categoryDropdown');
    if (dropdown && !dropdown.contains(event.target)) {
        const toggle = dropdown.querySelector('.dropdown-toggle');
        dropdown.classList.remove('active');
        if (toggle) {
            toggle.innerHTML = '상품 ▼';
        }
    }
});

