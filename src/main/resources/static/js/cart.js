//创建xmlHttp
var xmlHttp;
var lastQuantity;

function createXMLHttpRequest() {
    if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
        xmlHttp = new XMLHttpRequest();
    } else {// code for IE6, IE5
        xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
    }
}

//Ajax使用patch方式发送
function sendRequestUpdateCart(itemId, quantity) {
    createXMLHttpRequest();
    var queryString = "cart?type=update&";
    queryString = queryString + "workingItemId=" + itemId + "&itemQuantity=" + quantity;
    xmlHttp.onreadystatechange = handleStateChange;//监听，当服务器处理完毕时，更改TotalCost 和 SubTotal
    xmlHttp.open("PATCH", queryString, true);//true代表异步，不等待servlet返回结果
    xmlHttp.send(null);//用来表示这段信息，send(String)仅用于Post请求
}


function updateCart(itemId, value, last_quantity, totalQuantity) {
    var input = document.getElementById(itemId);
    if (lastQuantity == null) {
        lastQuantity = last_quantity;
    }
    // 判断能否被转化成数字
    if (isNaN(value)) {//在h5的number下全部都为number
        input.value = lastQuantity;
    } else {
        if (value < 1) {
            input.value = lastQuantity;
        } else if (value > totalQuantity) {
            lastQuantity = totalQuantity;
            input.value = totalQuantity;
        } else {
            lastQuantity = value;
            sendRequestUpdateCart(itemId, value);
        }
    }
}

function handleStateChange() {
    if (xmlHttp.readyState == 4 && xmlHttp.status == 200) {
        var response = xmlHttp.responseText;
        // console.log(response);

        var array = response.split("?");

        var div1 = document.getElementById(array[0] + "Total");
        div1.innerText = "$" + array[1];

        var div2 = document.getElementById("subTotal");
        div2.innerText = "$" + array[2];
    }
}

function defaultCheckout(){
    console.log("default");
    location.href = "order/accountInfo?status=default";
}

function continueCheckout(){
    if(confirm("You have an unfinished Order. Do you want to continue?")){
        console.log("continue");
        location.href = "order/accountInfo?status=continue";
    }else{
        defaultCheckout()
    }
}

$(function () {
    location.href = "#navigation";

    $rbtn = $(".btn-cartItem-remove");
    $rbtn.on('click',function () {
        $(this).css("visibility","hidden");
        $tr = $(this).parent().parent();
        $tr.addClass("danger");
        setTimeout(function(){
            $tr.addClass("animated fadeOutLeft");
            setTimeout(function () {
                removeCartItem();
                $tr.remove();
            },700)
        },700);

    });

    function removeCartItem() {
        var arr = $("table").find("tr");

        //在前台更新数据,仅在不是最后一项时
        if (arr.length >3){
            var total = $tr.children("td:nth-child(7)").text().trim();
            var itemTotal = total.substring(1, total.length);
            console.info(itemTotal);
            var subTotal = $("#subTotal").text().substring(1, total.length);
            var newSub = parseFloat(subTotal) - parseFloat(itemTotal);
            $("#subTotal").text("$" + newSub);
        }

        //在后台购物车删除货物
        var str = $tr.children(":first-child").text().trim();
        $.ajax({
            url : "/cart",
            type : "PATCH",
            dataType : "text",
            data : {
                workingItemId: str,
                type: 'remove'
            },
            success : function (data) {
                window.location.href = "cart";
                $("#new-cartItem-box").val("");
            }
        })

        if(arr.length <= 3) {
            console.info("购物车为空，刷新页面")
            window.location.href = "cart";
        }
    };

    $("#clearAll").click(function () {
        if (confirm("Confirm To CLear All Items?")){
            $("table").css("background-color","#fff5f5")

            setTimeout(function (){
                $("table").addClass("animated hinge");
                $.get("removeItemFromCart?workingItemId=ClearAll",function () {
                    setTimeout(function () {
                        window.location.href = "viewCart";
                    }, 1000);
                });
            },800);
        }

    });

    var flag = true;

    $("#new-cartItem-box").typeahead({
        source : function (query, process) {
            var keyword = query;
            $.ajax({
                url : "searchAutoComplete",
                type : "GET",
                dataType : "JSON",
                data : {
                    keyword : keyword
                },
                success : function (data) {

                    //如果传回来的就是name数组
                    var obj= eval(data);//解析json数据
                    var name = [];
                    $.each(obj, function (index, el) {
                        if(el == "CATS" || el =="BIRDS" || el =="DOGS" || el =="FISH" || el =="REPTILES"){
                        }else {
                            name.push(el);
                        }
                    })
                    console.log("返回值为:\n" + name)
                    process(name);
                }
            })
        },

        delay: 300,//在查找之间添加延迟

        fitToElement: false,//选项框宽度与输入框一致

        items: "all",//下拉选项中出现条目的最大数量。也可以设置为“all”

        autoSelect: true,//允许你决定是否自动选择第一个建议

        afterSelect: function (item) {//选中一条数据后的回调函数,jquery和typeahead会各触发一次
            if(flag){
                flag = false;
                console.log("add Item：'" + item + "' to Cart");
                setTimeout(function () {
                    $.ajax({
                        url : "cart",
                        type : "PATCH",
                        dataType : "text",
                        data : {
                            productName : item,
                            type: 'add',
                        },
                        success : function (data) {
                            window.location.href = "cart";
                            $("#new-cartItem-box").val("");
                        }
                    })
                }, 300);
            }else{
                flag = true;
            }
        }

    });


});