


function confirmOrder() {
    if(confirm("\nATTENTION:\nALL SALES FINAL NO RETURNS OR MODIFICATIONS")){
        $.post("/order/lineItems", function (data) {

            toastr.options = {
                "closeButton": true,
                "debug": false,
                "positionClass": "toast-top-center",
                "showDuration": "300",
                "hideDuration": "1000",
                "timeOut": "4500",
                "extendedTimeOut": "1000",
                "showEasing": "swing",
                "hideEasing": "linear",
                "showMethod": "fadeIn",
                "hideMethod": "fadeOut",
                "onclick": function () {
                    window.location.href = arr[0]
                }
            }

            var arr = data.split("|")
            console.log(data)

            if(arr[1] === ""){
                window.location.href = arr[0]
            }
            else{
                $.each(JSON.parse(arr[1]), function (index, obj) {
                    if(obj.quantity == 0){ //库存为空的情况
                        toastr.error("<div>货物 " + obj.itemId + " 库存为空，暂时无法购买<br>(5s)</div>")
                    }
                    else{ //库存不足的情况
                        toastr.warning("<div>货物 " + obj.itemId + " 库存不足，购买数量调整至 "
                            + obj.quantity +"<br>(5s)</div>")
                    }
                })

                setTimeout(function () {
                    window.location.href = arr[0]
                }, 5000)
            }
        })

        //模拟表单提交
        // var temp = document.createElement("form");
        // temp.action = '/order/lineItems';
        // temp.method = "post";
        // temp.style.display = "none";
        // document.body.appendChild(temp);
        // temp.submit();
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