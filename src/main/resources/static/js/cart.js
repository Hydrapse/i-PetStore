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
    var input = document.getElementsByName(itemId)[0];
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
    
    // 动态生成的元素要通过事件委托来处理，委托对象必须一直存在
    $("table").on('click', '.btn-cartItem-remove', function () {
        $(this).css("visibility","hidden");
        $tr = $(this).parent().parent();
        $tr.addClass("danger");

        setTimeout(function(){
            $tr.addClass("animated fadeOutLeft");
            setTimeout(function () {
                removeCartItem($tr.attr('id'));
                $tr.remove();
            },700)
        },700);

    });

    function removeCartItem(itemId) {
        var arr = $("table").find("tr");

        //在前台更新数据,仅在不是最后一项时
        if (arr.length > 3){
            var totalId = '#'+ itemId + 'Total'
            var itemTotal = parseFloat(formatCurrency($(totalId).text()))
            var newSub = parseFloat(formatCurrency($("#subTotal").text())) - itemTotal
            $("#subTotal").text("$" + formatCurrency(newSub));
        }

        //在后台购物车删除货物
        $.ajax({
            url : "/cart",
            type : "PATCH",
            dataType : "text",
            data : {
                workingItemId: itemId,
                type: 'remove'
            },
            success : function (data) {
                console.info("购物车: 货物" + itemId +"删除成功")
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

                $.ajax({
                    url : "/cart",
                    type : "PATCH",
                    dataType : "text",
                    data : {
                        workingItemId: "ClearAll",
                        type: 'remove'
                    },
                    success : function (data) {
                        console.info("清空购物车")
                        setTimeout(function () {
                            window.location.href = "cart";
                        }, 1000);
                    }
                })
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
                            $("#new-cartItem-box").val("");
                            var arr = data.split("|");

                            //toastr 提示框
                            toastr.options.timeOut = 1800
                            toastr.options.positionClass =  "toast-bottom-left"

                            if(arr[0] === 'lackItem'){
                                toastr.warning("库存不足，暂时无法购买")
                            }
                            else if(arr[0] === 'success'){
                                toastr.success("添加成功！")
                                // window.location.href = "cart";

                                var item = $.parseJSON(arr[1]);

                                var subTotal = parseFloat(formatCurrency($("#subTotal").text())) + item.listPrice
                                $("#subTotal").text('$' + formatCurrency(subTotal))

                                var isExist = false
                                $("tr").each(function () {
                                    if(this.id === item.itemId){
                                        isExist = true
                                    }
                                })

                                //直接添加至已有列表项
                                if(isExist){
                                    console.log("已经有重复项")
                                    var input = document.getElementsByName(item.itemId)[0];
                                    input.value++;

                                    var totalId = '#'+ item.itemId + 'Total'
                                    var itemTotal = parseFloat(formatCurrency($(totalId).text())) + item.listPrice
                                    $(totalId).text('$' + formatCurrency(itemTotal))
                                }
                                else{
                                    if(item.attribute1 === undefined){
                                        item.attribute1 = ""
                                    }
                                    if(item.attribute2 === undefined){
                                        item.attribute2 = ""
                                    }
                                    if(item.attribute3 === undefined){
                                        item.attribute3 = ""
                                    }
                                    if(item.attribute4 === undefined){
                                        item.attribute4 = ""
                                    }
                                    if(item.attribute5 === undefined){
                                        item.attribute5 = ""
                                    }

                                    //添加新行
                                    var arr = $("tr:eq(1)").find("td")
                                    if(arr.length <= 1){
                                        $("tr:eq(1)").hide()
                                    }
                                    $("tr:last").before(
                                        "           <tr id='" + item.itemId + "'>" +
                                        "                <td>" +
                                        "                    <a href=\"/item/" + item.itemId + "#navigation\">" +
                                        "                        " + item.itemId+
                                        "                    </a>" +
                                        "                </td>" +
                                        "                <td>" + item.product.productId + "</td>" +
                                        "                <td>" +
                                                            item.attribute1 + " " +item.attribute2 + " " +
                                                            item.attribute3 + " " + item.attribute4 + " " +
                                                            item.attribute5 + " " + item.product.name +
                                        "                </td>" +
                                        "                <td>" +
                                        "                    true" +
                                        "                </td>" +
                                        "                <td>" +
                                        "                    <form>" +
                                        "                        <input class=\"quantity\" type=\"number\" id=\"" + item.itemId + "\" name=\"" + item.itemId + "\"" +
                                        "                               value=\"1\" min=\"1\" max=\" " + item.quantity + "\"" +
                                        "                               onblur=\"updateCart(this.id, this.value, 1, " + item.quantity + ")\"/>" +
                                        "                    </form>" +
                                        "                </td>" +
                                        "                <td>$" + formatCurrency(item.listPrice) + "</td>" +
                                        "                <td id=\"" + item.itemId + "Total\">$" + formatCurrency(item.listPrice) +"</td>" +
                                        "                <td>" +
                                        "                    <a class=\"Button btn-cartItem-remove\" style=\"cursor: pointer;\">Remove</a>" +
                                        "                </td>" +
                                        "            </tr>");

                                    console.log($('#clearAll').length)
                                    //第一次添加时补上结账行
                                    if($('#clearAll').length <= 0){
                                        $("table").append("" +
                                            "<div>" +
                                            "        <a onclick=\"defaultCheckout()\" class=\"Button\" href=\"#\" style=\"padding: 7px;\"\n" +
                                            "           th:if=\"${session.order} == null\">" +
                                            "            Checkout&nbsp;" +
                                            "        </a>" +
                                            "        <a id=\"clearAll\" style=\"cursor: pointer; color: #d9534f;\"><i class=\"fa fa-trash fa-lg\"></i></a>\n" +
                                            "    </div>")
                                    }
                                }
                            }
                        }
                    })
                }, 300);
            }else{
                flag = true;
            }
        }

    });

    //设置悬浮窗
    $("[data-toggle='popover']").each(function () {
        var el = $(this)
        el.popover({
            html : true,
            title : function () {
                if(el.hasClass('empty-stack') || el.hasClass('lack-stack'))
                var data = "<a>"+ el.attr('id') +"</a>"
                return data
            },
            delay:{show:500, hide:50},
            content : function () {
                var data = null
                if(el.hasClass('empty-stack'))
                    data= "<span>该宠物暂无库存</span>"
                else if (el.hasClass('lack-stack'))
                    data = "<span>该宠物库存不足，数量调整至 <b>" + el.find('.quantity').val() +"</b></span>"
                return data
            },
            animation: true
        });
    })

});