$(function () {

    //选中特效
    $(".lightBlue").hover(function () {
        $(this).addClass("animated pulse");
        $(this).css("color", "floralwhite")
    },function(){
        $(this).removeClass("animated pulse");
        $(this).css("color", "")
    })

    //myBanner动画
    var $window = $(window);
    var $myBanner = $('.MyBanner');
    var endZone = $('#navBottom').offset().top - $window.height() - 200;

    $window.on('scroll', function() {
        //在白色区域变色
        if ($myBanner.offset().top + $myBanner.height() < $('#navBottom').offset().top){
            $('.MyBanner').css({"border-color" : "lightgrey","opacity" : "1"});
        }else{
            $('.MyBanner').css({"border-color" : "#FFF","opacity" : "0.9"});
        }
        if (endZone < $window.scrollTop()) {
            $myBanner.animate({ 'right': '0px' }, 250);
        } else {
            $myBanner.stop(true).animate({ 'right': '-360px' }, 250);
        }
    });

    //全局滤镜效果
    $(".showcase").hover(function () {
        $(this).css("filter", "none");
    },function () {
        $(this).css("filter"," brightness(90%)");
    });
});

//重新绘制主界面Map超链接区域
function reSizeMap(){
    var map = document.getElementById("MainMap")
    var element = map.childNodes;//小心空白节点
    var itemNumber = element.length / 2;

    var currentWidth = $("#MainImage").width();//获取当前图片宽度
    var currentHeight = $("#MainImage").height();//获取当前图片高度

    //跳过空白节点
    for (var i = 0; i < itemNumber - 1; i++) {
        var item = 2 * i + 1;
        var oldCoords = element[item].coords;
        var newCoords = adjustPosition(oldCoords, currentWidth);
        element[item].setAttribute("coords", newCoords);
    }

    //下次调整窗口基于这个来
    imageWidth = currentWidth;
    imageHeight = currentHeight;
}

//自动设置common 底边间距
window.onresize = function(){

    //判断是否为主界面
    if($("#MainImage").length > 0){
        console.log("resizeMap");
        reSizeMap();
    }

    // console.log("resizeBodyPadding");
    // var bottom = $(".bottom").height();
    // $("body").css("padding-bottom", bottom+200);
};
