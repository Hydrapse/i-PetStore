// var token = $("meta[name='_csrf']").attr("content");
// var header = $("meta[name='_csrf_header']").attr("content");
// $(document).ajaxSend(function(e, xhr, options) {
//     xhr.setRequestHeader(header, token);
// });

var xhr = new XMLHttpRequest();

//返回false代表登录失败，其他都代表登录成功（session里面有account）
//返回alreadyLogin代表已经登录
//返回history-cart-true-current-notEmpty代表原有购物车不为空，现在购物车也不为空
//返回history-cart-true-current-empty代表原有购物车不为空，现在购物车为空
//返回history-cart-false代表原有购物车为空
function submitLogin(){
    var username = document.getElementById("username").value;
    var password = document.getElementById("password").value;

    xhr.open("POST","login");
    xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xhr.send("username=" + username + "&password=" + password);

    //由ajax负责跳转页面
    xhr.onreadystatechange = function() {
        if (xhr.readyState==4 && xhr.status==200) {

            var arr = xhr.responseText.split("|");
            console.info(xhr.responseText);

            //判断登录是否成功
            if (arr[0] == "false") {
                alert("Login Failed！Please try again.")
                window.location.href = "login";
            }
            else if (arr[0] == "alreadyLogin"){
                window.location.href = "main.html";
            }
            else {
                var flag = false
                alert("Login Success!")

                //原有购物车不为空,现在购物车也不为空，需要判断
                if(arr[0] === "history-cart-true-current-notEmpty"){
                    var xhrTemp = new XMLHttpRequest();
                    if(confirm("Do you want to cover the current-shopping-cart with the historical-shopping-cart ?")){
                        xhrTemp.open("GET", "historicalCart?handleCart=true");
                        xhrTemp.send(null);
                        flag = true;
                    }
                }

                //原有购物车不为空，现在购物车为空，servlet中直接赋值，无需js操作
                //均进行转发
                var info = arr[1];
                console.log(info);
                if(info === "checkout"){
                    if(flag){
                        window.location.href = "cart";
                    }else{
                        window.location.href = "order/accountInfo";
                    }
                }else if(info == "checkOrderList"){
                    window.location.href = "checkOrderList";
                }else{
                    window.location.href = "main.html" ;
                }
                // sessionStorage.setItem("dispatcher", "main");//异步执行一定要小心呐
            }
        }
    }
}


$("#myVerificationModal").on('show.bs.modal', function(){
    var $this = $(this);
    var $modal_dialog = $this.find('.modal-dialog');
    $this.css('display', 'block');
    $modal_dialog.css({'margin-top': Math.max(0, ($(window).height() - $modal_dialog.height()) / 2) - 50});

});

$("#myVerificationModal").modal({backdrop: false, keyboard: false, show: false});

$("#registerModal").modal({backdrop: "static", keyboard: false, show: false});


$(function () {

    //ajax确认账户没被注册过
    $("#username-reg").blur(function () {
        var username = $("input[id$='username-reg']" ).val();

        if(username == null || username == ""){
            $("#usr-empty").css("visibility", "visible");
            $("#user-name-div").addClass("has-warning");
            document.getElementById("username-reg").placeholder = "用户名不为空";
            return;
        }

        var para = "username?username-reg=" + username;
        $.get(para, function (data) {
            if(data == "deny"){
                $("#usr-false").css("visibility", "visible");
                $("#user-name-div").addClass("has-error");
                document.getElementById("username-reg").value = "该用户名已被注册";
            }
            else if(data == "confirm"){
                $("#usr-true").css("visibility", "visible");
                $("#user-name-div").addClass("has-success");
            }
            else if(data == "empty"){
                $("#usr-empty").css("visibility", "visible");
                $("#user-name-div").addClass("has-warning");
                document.getElementById("username-reg").placeholder = "用户名不为空";
            }
        })
    });

    $("#username-reg").focus(function () {
        if (!$("#user-name-div").hasClass("has-success")){
            $("#username-reg").val("");
        }

        setTimeout(function(){
            $("#usr-true").css("visibility", "hidden");
            $("#usr-false").css("visibility", "hidden");
            $("#usr-empty").css("visibility", "hidden");
            $("#user-name-div").removeClass("has-success").removeClass("has-error")
                .removeClass("has-warning");
        },300);
    });

    $("#password-reg").bind("input oninput", function () {
        $("#password-reg2").val("");
        document.getElementById("password-reg2").placeholder = "Repeat Password";

        $("#pwd2-true").css("visibility", "hidden");
        $("#pwd2-false").css("visibility", "hidden");
        $("#pwd2-empty").css("visibility", "hidden");

        $("#pwd2-reg-div").removeClass("has-success").removeClass("has-error")
            .removeClass("has-warning");
    });
    // $("#password-reg").change(function () {
    //     $("#password-reg2").val("");
    //     document.getElementById("password-reg2").placeholder = "Repeat Password";
    //
    //     $("#pwd2-true").css("visibility", "hidden");
    //     $("#pwd2-false").css("visibility", "hidden");
    //     $("#pwd2-empty").css("visibility", "hidden");
    //
    //     $("#pwd2-reg-div").removeClass("has-success").removeClass("has-error")
    //         .removeClass("has-warning");
    // });
    $("#password-reg").blur(function () {
        var content = $("input[id$='password-reg']" ).val();

        if(content == null || content == ""){
            $("#pwd-empty").css("visibility", "visible");
            $("#pwd-reg-div").addClass("has-warning");
            document.getElementById("password-reg").placeholder = "密码不为空";
        }
        else {
            $("#pwd-true").css("visibility", "visible");
            $("#pwd-reg-div").addClass("has-success");
        }
    });

    $("#password-reg").focus(function () {
        if (!$("#pwd-reg-div").hasClass("has-success")){
            $("#password-reg").val("");
        }
        setTimeout(function(){
            $("#pwd-empty").css("visibility", "hidden");
            $("#pwd-true").css("visibility", "hidden");
            $("#pwd-reg-div").removeClass("has-success").removeClass("has-error")
                .removeClass("has-warning");
        },300);
    });

    $("#password-reg2").blur(function () {
        var content = $("input[id$='password-reg2']" ).val();

        if(content == null || content == ""){
            $("#pwd2-empty").css("visibility", "visible");
            $("#pwd2-reg-div").addClass("has-warning");
            document.getElementById("password-reg2").placeholder = "密码不为空";
        }
        else if (content !== $("#password-reg").val()){
            $("#pwd2-false").css("visibility", "visible");
            $("#pwd2-reg-div").addClass("has-error");
            $("#password-reg2").val("");
            document.getElementById("password-reg2").placeholder = "两次输入密码不相同";
        }
        else {
            $("#pwd2-true").css("visibility", "visible");
            $("#pwd2-reg-div").addClass("has-success");
        }
    });

    $("#password-reg2").focus(function () {
        if(!$("#pwd2-reg-div").hasClass("has-success")){
            $("#password-reg2").val("");
        }
        setTimeout(function(){
            $("#pwd2-true").css("visibility", "hidden");
            $("#pwd2-false").css("visibility", "hidden");
            $("#pwd2-empty").css("visibility", "hidden");
            $("#pwd2-reg-div").removeClass("has-success").removeClass("has-error")
                .removeClass("has-warning");
        },300);
    });
})

//验证注册信息是否正确
function checkRegisterForm(){

    $("#usr-true").css("visibility", "hidden");
    $("#usr-false").css("visibility", "hidden");
    $("#usr-empty").css("visibility", "hidden");
    $("#pwd-empty").css("visibility", "hidden");
    $("#pwd-true").css("visibility", "hidden");
    $("#pwd2-true").css("visibility", "hidden");
    $("#pwd2-false").css("visibility", "hidden");
    $("#pwd2-empty").css("visibility", "hidden");
    $("#user-name-div").removeClass("has-success").removeClass("has-error")
        .removeClass("has-warning");
    $("#pwd-reg-div").removeClass("has-success").removeClass("has-error")
        .removeClass("has-warning");
    $("#pwd2-reg-div").removeClass("has-success").removeClass("has-error")
        .removeClass("has-warning");
    if($("#username-reg").val() === "该用户名已被注册"){
        $("#username-reg").val("");
    }

    $("#username-reg").blur();
    $("#password-reg").blur();
    $("#password-reg2").blur();

    //延时等待
    setTimeout(function(){
        if($("#user-name-div").hasClass("has-success") && $("#pwd-reg-div").hasClass("has-success") && $("#pwd2-reg-div").hasClass("has-success")){
            $('#refresh-verify-btn').click();
            $("#myVerificationModal").modal("show");
        }
        else{
            $("#register-submit").addClass("animated shake").attr("disabled", true);
            setTimeout(function(){
                $("#register-submit").removeClass("shake").attr("disabled", false);

                //展示出错位置
                setTimeout(function () {
                    location.href ="#myModalLabel";
                }, 300);
            },700);

        }
    },300);

}

$("#username").bind("input oninput", function () {
    $("#password").val("");
});

$("form").submit(function () {
    $("#login-submit").click();
    return false;
});

$("#login-submit").click(function () {
    $('#refresh-verify-btn').click();
});

document.onkeypress = function (event) {
    var e = event || window.event;
    if (e && e.keyCode === 13) { //回车键的键值为13
        $("#goLogin").click();
    }
}





