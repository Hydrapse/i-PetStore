$(function() {
    var $window = $(window);
    var $nav = $("#navigation");
    var navHeight = $nav.innerHeight();
    var isEditAccount = $(".edit-account").length > 0;

    //设置hiddenTop
    if(isEditAccount){
        $(".containerFont").hide();
        $("#hiddenTop").show();
    }

    $window.on("scroll", function() {

        if ($window.scrollTop() > 99) {
            if(isEditAccount){
                //原版隐藏区
                $("#hiddenTop").css("margin-bottom", navHeight);
            }else{
                //新版炫酷隐藏区
                $(".containerFont").css("margin-bottom", navHeight);
            }

            $(".navbar-brand").css("color","");
            $nav.css({"background":"rgb(241,243,242)","color":"rgb(102, 187, 198)"});
            $nav.addClass("navbar-fixed-top").removeClass("navbar-static-top");
        }
        else{
            if(isEditAccount){
                //原版隐藏区
                $("#hiddenTop").css("margin-bottom", 0);
            }else {
                ////新版炫酷隐藏区
                $(".containerFont").css("margin-bottom", 0);
            }

            $(".navbar-brand").css("color","floralwhite");
            $nav.css({"background":"rgb(151,187,230)","color":"floralwhite"});
            $nav.addClass("navbar-static-top").removeClass("navbar-fixed-top");
        }

    });

    if(!$("#MainImage").length > 0){
        $(".containerFont h1").css("animation", "none")
            .css("color","rgb(154,190,233)");
    }

    $("#search-submit").click(function (e) {
        if($("#search-box").val() == null || $("#search-box").val() == ""){
            e.preventDefault();
        }
    })

});

