    function togglePasswordVisibility() {
        const password = document.getElementById("InputPassword");
        if (password.type === "password") {
        password.type = "text";
        }else {
            password.type = "password";
        }
    }
    function togglePasswordConfirmedVisibility(){
        const password = document.getElementById("ConfirmPassword");
        if (password.type === "password") {
        password.type = "text";
        }else {
            password.type = "password";
        }
    }

    function isPasswordValid(password) {
        const hasUppercase = !(password === password.toLowerCase());
        const hasLowercase = !(password === password.toUpperCase());
        const hasDigit = /\d/.test(password);
        return hasUppercase && hasLowercase && hasDigit;
    }

    function validateForm(event) {
        const password = document.getElementById('InputPassword').value;
        const passwordConfirmed = document.getElementById('ConfirmPassword').value;
        const passwordError = document.getElementById('passwordError');
        console.log("password "+password);
        console.log("conf "+passwordConfirmed);
        if (!isPasswordValid(passwordConfirmed)&&password!==passwordConfirmed) {
            event.preventDefault();
            passwordError.innerText = 'Incorrect password format. The password entered twice does not match.';
            passwordError.style.display = "block";
        } else if (!isPasswordValid(passwordConfirmed)) {
            event.preventDefault();
            passwordError.innerText = 'Incorrect password format. ';
            passwordError.style.display = "block";
        }else if(password!==passwordConfirmed){
            event.preventDefault();
            passwordError.innerText = 'The password entered twice does not match.'
            passwordError.style.display = "block";
        }
    }

    document.querySelector('form').addEventListener('submit', validateForm);
