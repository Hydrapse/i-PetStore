function signOut() {
    if(confirm("Are You Sure To Sign Out?")){
        window.location.href="signOut";
    }
}

//自动填充,只返回名字，name字符串
// var subjects = ['PHP', 'MySQL', 'SQL', 'PostgreSQL', 'HTML', 'CSS', 'HTML5', 'CSS3', 'JSON'];
// $('#search-box').typeahead({source: subjects})

$(function (){
    $("#search-box").typeahead({
        source : function (query, process) {
            var keyword = query;
            $.ajax({
                url : "/searchAutoComplete",
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
                        name.push(el);
                    })
                    console.log("返回值为:\n" + name)
                    process(name);
                }
            })
        },

        delay: 300,//在查找之间添加延迟

        fitToElement: false,//选项框宽度与输入框一致

        items: 5,//下拉选项中出现条目的最大数量。也可以设置为“all”

        autoSelect: false,//允许你决定是否自动选择第一个建议

        highlighter: function (item) { //显示处理(使用指定的方式，高亮(指出)匹配的部分)
            var name = item;
            if(item == "CATS" || item =="BIRDS" || item =="DOGS" || item =="FISH" || item =="REPTILES"){
                name = "<span style='font-weight: 700; font-size: 16px; color: #285a9a;'>CATEGORY: </span><span style='font-weight: 700; font-size: 16px;'>" + item + "</font>";
            }
            //数据列表中数据的显示方式
            return name ;
        },

        sorter : function (items) {
            //在java代码中已经排好序， 需要在这里重写一下，覆盖掉自带排序
            return items;
        }
    });
});



