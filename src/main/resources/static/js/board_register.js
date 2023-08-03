//유효성 검사
function validateTitleForm() {
    var title = document.getElementById('title').value;
    var titleError = document.getElementById('titleError');

    // 에러 메시지 초기화
    titleError.innerHTML = "";

    if (title.trim() === '') {
        titleError.innerHTML = "제목을 입력해주세요.";
        return false;
    }

    if (title.length > 45) {
        titleError.innerHTML = "제목은 최대 45자까지 입력 가능합니다."
        return false;
    }

    return true;
}

function validateContentForm() {
    var content = document.getElementById('content').value;
    var contentError = document.getElementById('contentError');

    // 에러 메시지 초기화
    contentError.innerHTML = "";

    if (content.trim() === '') {
        contentError.innerHTML = "내용을 입력해주세요.";
        return false;
    }

    if (content.length > 2000) {
        contentError.innerHTML = "내용은 최대 2000자까지 입력 가능합니다.";
        return false;
    }

    return true;
}

function updateSubmitButtonState() {
    var title = document.getElementById('title').value;
    var content = document.getElementById('content').value;

    var isTitleValid = title.trim() !== '' && title.length <= 45;
    var isContentValid = content.trim() !== '' && content.length <= 2000;

    // 글작성 버튼 상태 업데이트
    var submitButton = document.getElementById('submitButton');
    submitButton.disabled = !(isTitleValid && isContentValid);
}


// 글자 수 세기
function updateTitleCharacterCount() {
    var textarea = document.getElementById('title');
    var titleCharacterCount = document.getElementById('titleCharacterCount');

    // 입력된 내용의 글자 수를 계산
    var text = textarea.value;
    var characterLength = text.length;

    // 글자 수를 화면에 업데이트
    titleCharacterCount.innerHTML = characterLength + " / 45자";
}

function updateCharacterCount() {
    var textarea = document.getElementById('content');
    var characterCount = document.getElementById('characterCount');

    // 입력된 내용의 글자 수를 계산
    var text = textarea.value;
    var characterLength = text.length;

    // 글자 수를 화면에 업데이트
    characterCount.innerHTML = characterLength + " / 2000자";
}

// document.getElementById('title').addEventListener('input', updateSubmitButtonState);
// document.getElementById('content').addEventListener('input', function () {
//     updateCharacterCount();
//     updateSubmitButtonState();
// });

// 이미지 첨부시 미리보기
function previewImage(input) {
    if (input.files && input.files[0]) {
        var reader = new FileReader();

        reader.onload = function (e) {
            document.getElementById('previewImg').setAttribute('src', e.target.result);
            document.getElementById('imagePreview').style.display = 'block';
        }

        reader.readAsDataURL(input.files[0]);
    }
}

// 첨부한 이미지 삭제
function removeImage() {
    document.getElementById('image').value = ''; // 파일 선택을 초기화
    document.getElementById('previewImg').setAttribute('src', '');
    document.getElementById('imagePreview').style.display = 'none';
}

// 이미지 첨부 에러메시지
/*<![CDATA[*/
var errorMessage = /*[[${errorMessage}]]*/ null;
if (errorMessage) {
    alert(errorMessage);
}
/*]]>*/

// 엔터 키를 눌렀을 때 폼 제출 방지
function preventFormSubmit(event) {
    if (event.keyCode === 13) {
        event.preventDefault();
    }
}