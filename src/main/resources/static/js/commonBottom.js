
//黑科技！"123231231".toLocaleString('en-US') 123,231,231.0
//货币格式化
function formatCurrency(num) {
    num = num.toString().replace(/\$|\,/g,'');
    if(isNaN(num))
        num = "0";
    sign = (num == (num = Math.abs(num)));
    num = Math.floor(num*100+0.50000000001);
    cents = num%100;
    num = Math.floor(num/100).toString();
    if(cents<10)
        cents = "0" + cents;
    for (var i = 0; i < Math.floor((num.length-(1+i))/3); i++)
        num = num.substring(0,num.length-(4*i+3))+','+
            num.substring(num.length-(4*i+3));
    return (((sign)?'':'-') + num + '.' + cents);
}

//在商品列表页面添加至购物车
function addItemToCart(wid){
    toastr.options.timeOut = 3000
    toastr.options.positionClass =  "toast-bottom-left"
    toastr.options.onclick = function(){
        window.location.href = "/cart"
    }

    $.ajax({
        url : "/cart",
        type : "PATCH",
        dataType : "text",
        data : {
            workingItemId : wid,
            type: 'add',
        },
        success : function (data) {
            var arr = data.split("|");
            if(arr[0] === 'lackItem'){
                toastr.warning("库存不足，暂时无法购买")
            }
            else{
                toastr.success("添加成功！")
            }
        },
        error : function (e) {
            console.log(e.status)
            console.log(e.responseText)
        }

    })
}

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
