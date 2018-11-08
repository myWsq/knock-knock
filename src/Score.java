import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wsq
 * 2018/11/8 09:32
 * 记录用户得分
 */

public class Score implements Serializable {
    private static final int LIST_LENGTH = 5; // 排行榜容量
    private static final String JSON_PATH = "data.json"; // JSON文件路径
    private static final Type LIST_TYPE = new TypeToken<List<Item>>() {
    }.getType();
    List<Item> items;


    Score() {
        // 读取得分记录
        Gson gson = new Gson();
        try {
            FileReader fr = new FileReader(JSON_PATH);
            JsonReader jr = new JsonReader(fr);
            this.items = gson.fromJson(jr, LIST_TYPE);
            jr.close();
            fr.close();
        } catch (IOException error) {
            error.printStackTrace();
            this.items = new ArrayList<>();
        }
    }

    /**
     * 判断是产生了新纪录
     *
     * @param score 当前得分
     * @return 是否产生新纪录
     */
    public boolean isNewScore(int score) {
        int itemNum = 0;
        for (Item item : this.items) {
            itemNum++;
            if (score > item.score) {
                return true;
            }
        }

        return itemNum < LIST_LENGTH;
    }

    void push(int score) {
        boolean insertFlag = false; // 是否插入
        for (int i = 0; i < this.items.size(); i++) {
            if (score > this.items.get(i).score) {
                this.items.add(i, new Item(score));
                insertFlag = true;
                break;
            }
        }
        // 截取前十个记录
        if (items.size() >= LIST_LENGTH) {
            this.items = this.items.subList(0, LIST_LENGTH);
        } else if (!insertFlag) { // 如果排行榜没满
            this.items.add(new Item(score));
        }

        // 写入文件
        try {
            Writer writer = new FileWriter(JSON_PATH, false);
            Gson gson = new GsonBuilder().create();
            gson.toJson(this.items, writer);
            writer.close();
        } catch (IOException error) {
            error.printStackTrace();
        }

    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(items);
    }

}

class Item {
    int score;

    Item(int score) {
        this.score = score;
    }
}
