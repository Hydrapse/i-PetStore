
//原始图片的长宽
var imageWidth = 357;
var imageHeight = 347;

//调整MAP中坐标
function adjustPosition(position, currentWidth) {
    var each = position.split(",");

    //分别将x坐标、y坐标、半径 乘以一个系数，且图片长宽比始终不变
    for (var i = 0; i < each.length; i++) {
        each[i] = Math.round(parseInt(each[i]) * currentWidth / imageWidth).toString();//x坐标
    }
    //生成新的坐标点
    var newPosition = "";
    for (var i = 0; i < each.length; i++) {
        newPosition += each[i];
        if (i < each.length - 1) {
            newPosition += ",";
        }
    }

    return newPosition;
}

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



$(function(){

    //dom就绪时初始化Map
    reSizeMap()

    //设置悬浮窗
    $("[data-toggle='popover']").popover({
        html : true,
        title : "<a href='viewLoginForm'>LOGIN NOW</a>",
        delay:{show:300, hide:700},
        content : "<span>Be a slave of your pet </span><i class='fa fa-thumbs-up'></i>",
        animation: true
    });


    $("#img-bigBird").hover(
        function (e) {
            $("body").append("<div id='preview1' class='preview'>" +
                "<h3 class='popover-title'><a style='font-size: 18px; font-weight: bold; color: #5395e2;'>GREEN BIG PARROT </a> </h3>" +
                "<div class='preview-content'><span>Will only imitate others <i class='fa fa-rocket fa-lg'></i></span></div>" +
                "</div>");
            $("#preview1").css("left", e.pageX).css("top", e.pageY-150)
                .css("display", "none").fadeIn(300);
        },
        function () {
            $("#preview1").remove();
            // $("#preview1").fadeOut(300, function () {
            //     $(this).remove();
            // });
        }
    )

    $("#img-fish").hover(
        function (e) {
            console.log($(this).offset().top);
            console.log(e.pageX)
            $("body").append("<div id='preview2' class='preview'><h3 class='popover-title'><a>Golden Fish</a></h3><div class='preview-content'><span>Saltwater, Freshwater </span></div></div>");
            $("#preview2").css("left", e.pageX-250).css("top", e.pageY)
                .css("display", "none").fadeIn(300);
        },
        function () {
            $("#preview2").remove();
            // $("#preview2").fadeOut(300, function () {
            //     $(this).remove();
            // });
        }
    )

    $("#img-dog").hover(
        function (e) {
            console.log($(this).offset().top);
            console.log(e.pageX)
            $("body").append("<div id='preview3' class='preview'><h3 class='popover-title'><a>Little Puppy</a></h3><div class='preview-content'><span>Various Breeds </span></div></div>");
            $("#preview3").css("left", e.pageX-130).css("top", e.pageY+80)
                .css("display", "none").fadeIn(300);
        },
        function () {
            $("#preview3").remove();
            // $("#preview3").fadeOut(300, function () {
            //     $(this).remove();
            // });
        }
    )

    $("#img-reptile").hover(
        function (e) {
            console.log($(this).offset().top);
            console.log(e.pageX)
            $("body").append("<div id='preview4' class='preview'><h3 class='popover-title'><a>Cold Bloody!</a></h3><div class='preview-content'><span>Lizards, Turtles, Snakes</span></div></div>");
            $("#preview4").css("left", e.pageX-100).css("top", e.pageY+80)
                .css("display", "none").fadeIn(300);
        },
        function () {
            $("#preview4").remove();
            // $("#preview4").fadeOut(300, function () {
            //     $(this).remove();
            // });
        }
    )

    $("#img-cat").hover(
        function (e) {
            console.log($(this).offset().top);
            console.log(e.pageX)
            $("body").append("<div id='preview5' class='preview'><h3 class='popover-title'><a>Lovely Kitten</a></h3><div class='preview-content'><span>Various Breeds, Exotic Varieties</span></div></div>");
            $("#preview5").css("left", e.pageX+50).css("top", e.pageY+50)
                .css("display", "none").fadeIn(300);
        },
        function () {
            $("#preview5").remove();
            // $("#preview5").fadeOut(300, function () {
            //     $(this).remove();
            // });
        }
    )

    $("#img-bird").hover(
        function (e) {
            console.log($(this).offset().top);
            console.log(e.pageX)
            $("body").append("<div id='preview6' class='preview'><h3 class='popover-title'><a>Wtf? Blue parrot?</a></h3><div class='preview-content'><span>I've seen u before....<i class='fa fa-thumbs-down'></i></span></div></div>");
            $("#preview6").css("left", e.pageX+50).css("top", e.pageY)
                .css("display", "none").fadeIn(300);
        },
        function () {
            $("#preview6").remove();
            // $("#preview6").fadeOut(300, function () {
            //     $(this).remove();
            // });
        }
    )
});

//设置滤镜效果