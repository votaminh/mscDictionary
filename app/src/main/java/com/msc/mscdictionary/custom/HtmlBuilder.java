package com.msc.mscdictionary.custom;

import android.util.Log;

import com.msc.mscdictionary.util.Constant;

public class HtmlBuilder {
    float ratioSize;
    String header, endTag;
    String html;
    String meanUser;
    String tagBr = "";

    public HtmlBuilder(float ratioSize, String html, String meanUser){
        this.ratioSize = ratioSize;
        this.html = html;

        for (int i = 0; i < 1/ratioSize * Constant.NUMBER_COUNT_BR; i++) {
            tagBr += Constant.TAG_BR;
        }

        this.meanUser = "<div class=\"bg-grey bold font-large m-top20\">\n" +
                "    <span>Nghĩa thông dụng</span>\n" +
                "</div> \n" +
                "<div class=\"green bold margin25 m-top15\">\n" +
                meanUser +
                "</div> \n" +
                "<div id=\"edit_mean\">\n" +
                "    <a style=\"color: #2196f3;\" href=\"editMean\" >Chỉnh sửa nghĩa thông dụng</a>\n" +
                "</div>" ;

        header = "<html>\n" +
                "<header>\n" +
                "    <style>\n" +
                "        a,\n" +
                "        a:hover {\n" +
                "            text-decoration: none\n" +
                "        }\n" +
                "\n" +
                "        #header,\n" +
                "        .info_app,\n" +
                "        .wrapper {\n" +
                "            position: relative\n" +
                "        }\n" +
                "\n" +
                "        .clear,\n" +
                "        .clearfix:after,\n" +
                "        .clr {\n" +
                "            clear: both\n" +
                "        }\n" +
                "\n" +
                "        .info_l ul li,\n" +
                "        .intro-lbd ul li {\n" +
                "            list-style-image: url(\"https://raw.githubusercontent.com/votaminh/DataStore/master/dictionaryApp/ic_dot.png\")\n" +
                "        }\n" +
                "\n" +
                "        .getcode p,\n" +
                "        a {\n" +
                "            word-wrap: break-word\n" +
                "        }\n" +
                "\n" +
                "        a,\n" +
                "        abbr,\n" +
                "        acronym,\n" +
                "        address,\n" +
                "        applet,\n" +
                "        b,\n" +
                "        big,\n" +
                "        blockquote,\n" +
                "        body,\n" +
                "        caption,\n" +
                "        center,\n" +
                "        cite,\n" +
                "        code,\n" +
                "        dd,\n" +
                "        del,\n" +
                "        dfn,\n" +
                "        div,\n" +
                "        dl,\n" +
                "        dt,\n" +
                "        em,\n" +
                "        fieldset,\n" +
                "        font,\n" +
                "        form,\n" +
                "        h1,\n" +
                "        h2,\n" +
                "        h3,\n" +
                "        h4,\n" +
                "        h5,\n" +
                "        h6,\n" +
                "        html,\n" +
                "        i,\n" +
                "        iframe,\n" +
                "        img,\n" +
                "        input,\n" +
                "        ins,\n" +
                "        kbd,\n" +
                "        legend,\n" +
                "        li,\n" +
                "        object,\n" +
                "        ol,\n" +
                "        p,\n" +
                "        pre,\n" +
                "        q,\n" +
                "        s,\n" +
                "        samp,\n" +
                "        small,\n" +
                "        span,\n" +
                "        strike,\n" +
                "        strong,\n" +
                "        sub,\n" +
                "        tbody,\n" +
                "        textarea,\n" +
                "        tfoot,\n" +
                "        th,\n" +
                "        thead,\n" +
                "        tt,\n" +
                "        u,\n" +
                "        ul,\n" +
                "        var {\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            border: 0;\n" +
                "            outline: 0;\n" +
                "            font-size: 100%;\n" +
                "            background: 0 0\n" +
                "        }\n" +
                "\n" +
                "        body {\n" +
                "            line-height: 1;\n" +
                "            font-size: " + ratioSize * 12 + "px;\n" +
                "            color: #3d3d3d;\n" +
                "            font-family: Tahoma, Arial;\n" +
                "            background-color: #fff\n" +
                "        }\n" +
                "\n" +
                "        .bold,\n" +
                "        .tab a {\n" +
                "            font-weight: 700;\n" +
                "            font-size : " + ratioSize * 1.4 * 12 +"px\n" +
                "        }\n" +
                "        \n" +
                "        .content div {\n" +
                "            margin: 7px 0\n" +
                "        }\n" +
                "\n" +
                "        .normal {\n" +
                "            font-weight: 400\n" +
                "        }\n" +
                "\n" +
                "    \n" +
                "        .bg-grey {\n" +
                "            margin: 5px 0\n" +
                "        }\n" +
                "\n" +
                "        .bg-grey span {\n" +
                "            padding: 5px;\n" +
                "            background-color: #1473e7;\n" +
                "            color : #fff\n" +
                "        }\n" +
                "\n" +
                "        .dot-blue,\n" +
                "        .green,\n" +
                "        .grey {\n" +
                "            padding-left: 15px\n" +
                "        }\n" +
                "\n" +
                "        .green {\n" +
                "            background: url(https://raw.githubusercontent.com/votaminh/DataStore/master/dictionaryApp/ic_dot.png) no-repeat;\n" +
                "            background-size: 8px 19px\n" +
                "        }\n" +
                "\n" +
                "        .dot-blue {\n" +
                "            background-color: #e3edfb;\n" +
                "        \n" +
                "        }\n" +
                "\n" +
                "        .grey {\n" +
                "            background: url(https://raw.githubusercontent.com/votaminh/DataStore/master/dictionaryApp/ic_dot.png) no-repeat;\n" +
                "            background-size: 8px 19px\n" +
                "        }\n" +
                "\n" +
                "        .color-black {\n" +
                "            color: #000 !important\n" +
                "        }\n" +
                "\n" +
                "        .color-blue,\n" +
                "        .color-blue>a {\n" +
                "            color: #0a7be9 !important\n" +
                "        }\n" +
                "\n" +
                "        .color-light-blue,\n" +
                "        .color-light-blue>a {\n" +
                "            color: #1198b6 !important\n" +
                "        }\n" +
                "\n" +
                "        .color-grey {\n" +
                "            color: #777 !important\n" +
                "        }\n" +
                "\n" +
                "        .color-light-grey {\n" +
                "            color: #bbb !important\n" +
                "        }\n" +
                "\n" +
                "        .color-dark-grey {\n" +
                "            color: #555 !important\n" +
                "        }\n" +
                "\n" +
                "        .color-orange {\n" +
                "            color: #D67104 !important\n" +
                "        }\n" +
                "\n" +
                "        .margin15 {\n" +
                "            margin-left: 15px !important\n" +
                "        }\n" +
                "\n" +
                "        .margin25 {\n" +
                "            margin-left: 25px !important\n" +
                "        }\n" +
                "\n" +
                "        .m-top10 {\n" +
                "            margin-top: 10px !important\n" +
                "        }\n" +
                "\n" +
                "        .m-top15 {\n" +
                "            margin-top: 15px !important\n" +
                "        }\n" +
                "\n" +
                "        .m-top20 {\n" +
                "            margin-top: 20px !important\n" +
                "        }\n" +
                "#edit_mean{\n" +
                "            margin-top: 10px;\n" +
                "        }"+
                "    </style>\n" +
                "</header>\n" +
                "\n" +
                "<body>";
        endTag = "<script>\n" +
                "        var elems = document.querySelectorAll('.find_word');\n" +
                "\n" +
                "        for (var i = elems.length; i--;) {\n" +
                "            elems[i].addEventListener('click', fn, false);\n" +
                "        }\n" +
                "\n" +
                "        function fn() {\n" +
                "            window.open( \n" +
                "              \"https://dict.laban.vn/find?type=1&query=\" + this.rel, \"_top\"); \n" +
                "        }\n" +
                "    </script> </body>";
    }

    public String get(){
        String h = header+ tagBr + meanUser + html + endTag;
        return h;
    }
}
