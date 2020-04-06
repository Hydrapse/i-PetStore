var xmlHttp = new XMLHttpRequest();

var ver_times = 0;

//验证码css 已使用Thymeleaf添加
// $("<link>")
//     .attr({ rel: "stylesheet",
//         type: "text/css",
//         href: "css/verification.css"
//     })
//     .appendTo("head");

//验证码拼图，自调函数立即执行，不会产生全局变量，安全
(function (window, document) {
    //构造函数语法
    var SliderBar = function (targetDom, options) {

        // 判断是用函数创建的还是用new创建的。这样我们就可以通过MaskShare("dom") 或 new MaskShare("dom")来使用这个插件了
        if (!(this instanceof SliderBar)) return new SliderBar(targetDom, options);
        // 参数
        this.options = this.extend({
            dataList: []
        }, options);
        // 获取dom
        this.targetDom = document.getElementById(targetDom);

        var dataList = this.options.dataList;
        if (dataList.length > 0) {

            this.targetDom.innerHTML =
                "<div style='margin-bottom: 10px;'>" +
                "<div class='slide-img-div'>" +
                "<div class='slide-img-nopadding'>" +
                "<img class='slide-img' id='slideImg' src='' />" +
                "<div class='slide-block' id='slideBlock'></div>" +
                "<div class='slide-box-shadow' id='cutBlock'></div>" +
                "</div>" +
                "<div class='scroll-background  slide-img-hint-info' id='slideHintInfo'>" +
                "<div class='slide-img-hint'>" +
                "<div class='scroll-background slide-icon' id='slideIcon'></div>" +
                "<div class='slide-text'>" +
                "<span class='slide-text-type' id='slideType' style='color: floralwhite;'></span>" +
                "<span class='slide-text-content' id='slideContent'></span>" +
                "</div>" +
                "</div>" +
                "</div>" +
                "</div>" +
                "<div class='scroll-background scroll-bar'>" +
                "<div class='scroll-background slide-btn' id='slideBtn'></div>" +
                "<div class='slide-title' id='slideHint'> 拖动左边滑块完成上方拼图 </div>" +
                "</div>" +
                "</div>";
            this.slideBtn = document.getElementById("slideBtn");                 // 拖拽按钮
            this.refreshBtn = document.getElementById("refresh-verify-btn");     // 换图按钮
            this.slideHint = document.getElementById("slideHint");               // 提示名称
            this.slideImg = document.getElementById("slideImg");                 // 图片
            this.cutBlock = document.getElementById("cutBlock");                 // 裁剪区域
            this.slideBlock = document.getElementById("slideBlock");             // 裁剪的图片
            this.slideIcon = document.getElementById("slideIcon");               // 正确、失败的图标
            this.slideType = document.getElementById("slideType");               // 正确、失败
            this.slideContent = document.getElementById("slideContent");         // 正确、失败的正文
            this.slideHintInfo = document.getElementById("slideHintInfo");       // 弹出
            this.resultX = 0;
            this.startX = 0;
            this.timer = 0;
            this.startTamp = 0;
            this.endTamp = 0;
            this.x = 0;
            this.imgWidth = 0;
            this.imgHeight = 0;
            this.imgList = [];
            this.isSuccess = true;
            for (var i = 1; i < 10; i++) {
                this.imgList.push("v" + i + ".png");
            }
        }
        this.init();
    }
    //更改原型对象
    SliderBar.prototype = {
        init: function () {
            this.event();
        },
        extend: function (obj, obj2) {
            //循环遍历obj2的属性，将其赋给obj1
            for (var k in obj2) {
                obj[k] = obj2[k];
            }
            return obj;
        },
        event: function () {
            var _this = this;
            _this.reToNewImg();
            _this.slideBtn.onmousedown = function(event){
                _this.mousedown(_this, event);
            }
            _this.refreshBtn.onclick = function(){
                _this.refreshBtnClick(_this);
            }
        },
        refreshBtnClick: function(_this){
            _this.isSuccess = true;
            _this.slideBlock.style.cssText = "";
            _this.cutBlock.style.cssText = "";
            _this.reToNewImg();
        },
        reToNewImg: function () {
            var _this = this;
            var index = Math.round(Math.random() * 8);         // 该方法有等于0 的情况
            var imgSrc = "images/" + _this.imgList[index] + "";
            _this.slideImg.setAttribute("src", imgSrc);
            _this.slideBlock.style.backgroundImage = "url("+ imgSrc +")";
            _this.slideImg.onload = function (e) {
                e.stopPropagation();
                _this.imgWidth = _this.slideImg.offsetWidth;                   // 图片宽
                _this.imgHeight = _this.slideImg.offsetHeight;                 // 图片高
            }
        },
        cutImg: function () {
            var _this = this;
            _this.cutBlock.style.display = "block";
            var cutWidth = _this.cutBlock.offsetWidth;                // 裁剪区域宽
            var cutHeight = _this.cutBlock.offsetHeight;              // 裁剪区域高
            // left
            _this.resultX = Math.floor(Math.random() * (_this.imgWidth - cutWidth * 2 - 4) + cutWidth);
            // top
            var cutTop = Math.floor(Math.random() * (_this.imgHeight - cutHeight * 2) + cutHeight);
            // 设置样式
            _this.cutBlock.style.cssText = "top:" + cutTop + "px;" + "left:" + _this.resultX + "px; display: block;";
            _this.slideBlock.style.top = cutTop + "px";
            _this.slideBlock.style.backgroundPosition = "-" + _this.resultX + "px -" + cutTop + "px";
            _this.slideBlock.style.opacity = "1";
        },
        mousedown: function (_this, e) {

            $("#verify-content").removeClass("animated shake");

            e.preventDefault();
            _this.startX = e.clientX;
            _this.startTamp = (new Date()).valueOf();
            var target = e.target;
            target.style.backgroundPosition = "0 -216px";
            _this.slideHint.style.opacity = "0";
            if(_this.isSuccess){
                _this.cutImg();
            }
            document.addEventListener('mousemove', mousemove);
            document.addEventListener('mouseup', mouseup);

            // 拖拽
            function mousemove(event) {
                _this.x = event.clientX - _this.startX;
                if (_this.x < 0) {
                    _this.slideBtn.style.left = "0px";
                    _this.slideBlock.style.left = "2px";
                } else if (_this.x >= 0 && _this.x <= 217) {
                    _this.slideBtn.style.left = _this.x + "px";
                    _this.slideBlock.style.left = _this.x + "px";
                } else {
                    _this.slideBtn.style.left = "217px";
                    _this.slideBlock.style.left = "217px";
                }
                _this.slideBtn.style.transition = "none";
                _this.slideBlock.style.transition = "none";
            };
            // 鼠标放开
            function mouseup() {
                document.removeEventListener('mousemove', mousemove);
                document.removeEventListener('mouseup', mouseup);
                var left = _this.slideBlock.style.left;
                left = parseInt(left.substring(0, left.length-2));
                if(_this.resultX > (left - 2) && _this.resultX < (left + 2)){
                    _this.isSuccess = true;
                    _this.endTamp = (new Date()).valueOf();
                    _this.timer = ((_this.endTamp - _this.startTamp) / 1000).toFixed(1);
                    // 裁剪图片(拼图的一块)
                    _this.slideBlock.style.opacity = "0";
                    _this.slideBlock.style.transition = "opacity 0.6s";
                    // 裁剪的区域(黑黑的那一块)
                    _this.cutBlock.style.opacity = "0";
                    _this.cutBlock.style.transition = "opacity 0.6s";
                    // 正确弹出的图标
                    _this.slideIcon.style.backgroundPosition = "0 -1207px";
                    $("#slideHintInfo").css("background", "rgb(94,191,113)");
                    // _this.slideType.className = "slide-text-type greenColor";
                    _this.slideType.innerHTML = "验证通过:";
                    _this.slideContent.innerHTML = "用时" + _this.timer + "s";
                    setTimeout(function(){
                        _this.cutBlock.style.display = "none";
                        _this.slideBlock.style.left = "2px";
                        _this.reToNewImg();
                    }, 600);
                    _this.options.success&&_this.options.success();
                }else{
                    _this.isSuccess = false;
                    // 设置样式
                    // 裁剪图片(拼图的一块)
                    _this.slideBlock.style.left = "2px";
                    _this.slideBlock.style.transition = "left 0.6s";
                    // 错误弹出的图标
                    _this.slideIcon.style.backgroundPosition = "0 -1229px";
                    $("#slideHintInfo").css("background", "rgb(223,114,91)");
                    // _this.slideType.className = "slide-text-type redColor";
                    _this.slideType.innerHTML = "验证失败:";
                    _this.slideContent.innerHTML = "拖动滑块将悬浮图像正确拼合";
                    _this.options.fail&&_this.options.fail();
                }
                // 设置样式
                _this.slideHintInfo.style.height = "22px";
                setTimeout(function(){
                    _this.slideHintInfo.style.height = "0px";
                }, 1300);
                _this.slideBtn.style.backgroundPosition = "0 -84px";
                _this.slideBtn.style.left = "0";
                _this.slideBtn.style.transition = "left 0.6s";
                _this.slideHint.style.opacity = "1";
            }
        }
    }
    window.SliderBar = SliderBar;
}(window, document));

var dataList = ["0","1"];
var options = {
    dataList: dataList,
    success:function(){
        console.log("verification success");

        //确认验证通过
        xmlHttp.open("POST","verification");
        xmlHttp.send(null);

        setTimeout(function(){
            $("#account-info-form").submit();
        },1000);

    },
    fail: function(){
        $("#verify-content").addClass("animated shake");
        console.log("verification fail");
        if(ver_times >= 2){
            setTimeout(function(){
                $("#refresh-verify-btn").click();
            },1000);
            ver_times = -1;
        }
        ver_times++;
    }
};

SliderBar("slideBar", options);

$("#close-verify-btn").click(function () {
    $("#verify-content").removeClass("animated shake");
    ver_times = 0;
});

$(".submit-edit-account").click(function () {
    $("#refresh-verify-btn").click();
});

$("#myVerificationModal").on('show.bs.modal', function(){
    var $this = $(this);
    var $modal_dialog = $this.find('.modal-dialog');
    $this.css('display', 'block');
    $modal_dialog.css({'margin-top': Math.max(0, ($(window).height() - $modal_dialog.height()) / 2) - 50});

});

$("#myVerificationModal").modal({backdrop: false, keyboard: false, show: false});

$(function () {

    var videoAccount = document.getElementById("videoAccount");

    $(".ed-row-2").hover(function () {
        // $("#videoAccount").css("filter","none");
        videoAccount.play();
    },function () {
        videoAccount.pause();
    })

    var profileGif = document.getElementsByName("favouriteCategoryId");
    var str = "images/" + profileGif[0].options[profileGif[0].options.selectedIndex].value.toLowerCase() + "_walk.gif";
    $("#profileGif").attr("src", str);

    $("#favCategory").change(function () {
        var temp = "images/" + profileGif[0].options[profileGif[0].options.selectedIndex].value.toLowerCase() + "_walk.gif";
        if(temp !== str){
            str = temp;
            $("#profileGif").attr("src", str);
        }
    })
});