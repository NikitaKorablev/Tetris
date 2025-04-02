package com.example.tetrisnoactivity.models;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.example.tetrisnoactivity.constants.FieldConstants;

import java.util.Random;

//@Serializable
public class Block {
    private final int shapeIndex; //индекс формы блока
    private int frameNumber; //количество фреймов
    private final BlockColor color;
    private Point position; //текущая позиция на поле
    public Block(int shapeIndex, BlockColor blockColor) {
        this.frameNumber = 0;
        this.shapeIndex = shapeIndex;
        this.color = blockColor;
        this.position = new Point(FieldConstants.COLUMN_COUNT.getValue()/2, 0);
    }
    @NonNull
    public static Block createBlock() {
        // Shape.values() - возвращает все елементы которые хранит Shape
        // Shape.values().lenght - количество элементов в Shape

        Random random = new Random();
        int shapeIndex = random.nextInt(Shape.values().length); //выбираем индекс блока
        BlockColor blockColor = BlockColor.values()
                [random.nextInt(BlockColor.values().length)]; //выбираем цвет блока
        Block block = new Block(shapeIndex, blockColor); //создаем новый блок с
                                                         // нужными характеристиками
        block.position.x -= Shape.values()[shapeIndex].getStartPosition();
        return block;
    }
    public enum BlockColor {
        PINK(Color.rgb(255, 105,180), (byte) 2),
        GREEN(Color.rgb(0, 128, 0), (byte) 3),
        ORANGE(Color.rgb(255, 140, 0), (byte) 4),
        YELLOW(Color.rgb(255, 255, 0), (byte) 5),
        CYAN(Color.rgb(0, 255, 255), (byte) 6);

        BlockColor(int rgbValue, byte value) {
            this.rgbValue = rgbValue;
            this.byteValue = value;
        }
        private final int rgbValue;
        private final byte byteValue;
    }

    public Bundle toBundle() {
        Bundle block = new Bundle();
        block.putString("frameNumber", String.valueOf(frameNumber));
        block.putString("shapeIndex", String.valueOf(shapeIndex));
        block.putString("color", String.valueOf(color.rgbValue));
        block.putString("p.x", String.valueOf(position.x));
        block.putString("p.y", String.valueOf(position.y));

        return block;
    }
    public static int getColor(byte value) {
        for (BlockColor colour: BlockColor.values()) {
            if (value == colour.byteValue) {
                return colour.rgbValue;
            }
        }
        return -1;
    }
    public final void setState(int frame, Point position) {
        this.frameNumber = frame;
        this.position = position;
    }
    public int getShapeIndex() { return this.shapeIndex; }
    @NonNull
    public final byte[][] getShape(int frameNumber) {
        return Shape.values()[shapeIndex].getFrame(frameNumber).as2dByteArray();
    }
    public Point getPosition() {
        return this.position;
    }
    public final int getFrameCount() {
        return Shape.values()[shapeIndex].getFrameCount();
    }
    public int getFrameNumber() {
        return frameNumber;
    }
    public int getColor() {
        return color.rgbValue;
    }
    public byte getStaticValue() {
        return color.byteValue;
    }
}
