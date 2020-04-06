
function confirmOrder() {
    if(confirm("\nATTENTION:\nALL SALES FINAL NO RETURNS OR MODIFICATIONS")){
        // window.location.href="confirmLineItems";
        // $.post("/order/lineItems") //不想用ajax

        //模拟表单提交
        var temp = document.createElement("form");
        temp.action = '/order/lineItems';
        temp.method = "post";
        temp.style.display = "none";
        document.body.appendChild(temp);
        temp.submit();
    }
}

$(function () {
    $("#shippingAddressRequired").change(function () {
        if($(this).is(':checked')){
            $("#shipAddressTable").css('display','inline-table').addClass('animated fadeIn')
            $(this).parent().parent().hide()
        }
        if(!$(this).is(':checked')){
            $("#shipAddressTable").css('display','none')
        }
    })
})