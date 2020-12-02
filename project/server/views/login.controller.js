angular
    .module('loginApp', [])
    .controller('LogInController', ['$scope', '$http', LogInController]);

function LogInController($scope, $http) {

    var globalSocket = io();
    
    var loginVM = this;

    loginVM.onClickLogIn = () => {

        let userID = $('#ID_USERID_INPUT').val();
        if (userID.length === 0) {
            return alert('ID를 입력해주세요.');
        }

        let password = $('#ID_PASSWORD_INPUT').val();

        if (password.length === 0) {
            return alert('PW를 입력해주세요.');
        }

        $http.post('/api/adminLogIn', {
            mName: userID,
            mPassWord: password
        })
            .success(function (data, status, headers, config) {
                if (data.r === true) {

                    nextTargetUrl = data.d;
                    location.replace(nextTargetUrl);

                } else {
                    alert(data.m);
                }
            })
            .error(function (data, status, header, config) {
                alert(data.message);
            });
    }

    // Initialize
    angular.element(document).ready(function () {
    });
}
