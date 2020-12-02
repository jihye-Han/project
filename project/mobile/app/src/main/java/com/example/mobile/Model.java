package com.example.mobile;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Model {

    public static class Cheer {
        public int xCount;
        public int yCount;
        public int x;
        public int y;
        public String color;

        public Cheer() {
            this.xCount = 0;
            this.yCount = 0;
            this.x = 0;
            this.y = 0;
            this.color = "";
        }
    }


    public static class SerializeModel {

        public static class CheerItemList {
            @SerializedName("d")
            public ArrayList<CheerItem> items;
        }

        public static class CheerItem {
            @SerializedName("index")
            public String index;

            @SerializedName("color")
            public String color;
        }

        public static class CheerItemInfo {
            @SerializedName("d")
            public CheerItem item;
        }
    }

}
