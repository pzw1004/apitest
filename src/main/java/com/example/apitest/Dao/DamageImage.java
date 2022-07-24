package com.example.apitest.Dao;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.util.List;

/**
 * @Author 宋宗垚
 * @Date 2019/7/20 22:28
 * @Description 具有损伤的图片类
 */
public class DamageImage {

    private Integer id;
    private String sourceImagePath;// 原始的数据位置，为兼容性，这里可以是tif图片的路径也可以是DICOM图片的路径
    private String transferImagePath; // 转化图片的路径，为了图片的方便使用，通常将tif或者DIOCM转化为png或者jpg
    private List<DamageData> damageDataList;

    private Integer width;
    private Integer height;

//    public DamageImage(String imagePath, List<DamageData> damageDataList) {
//        this.imagePath = imagePath;
//        this.damageDataList = damageDataList;
//    }

    public DamageImage() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getSourceImagePath() {
        return sourceImagePath;
    }

    public void setSourceImagePath(String sourceImagePath) {
        this.sourceImagePath = sourceImagePath;
    }

    public String getTransferImagePath() {
        return transferImagePath;
    }

    public void setTransferImagePath(String transferImagePath) {
        this.transferImagePath = transferImagePath;
    }

    public List<DamageData> getDamageDataList() {
        return damageDataList;
    }

    public void setDamageDataList(List<DamageData> damageDataList) {
        this.damageDataList = damageDataList;
    }

    /**
     * 将对象中的数据生成xml文件。
     * @param floderPath 存放文件的文件夹的路径，
     * @return 是否成功
     */
    public void generateXMLFile(String floderPath) throws IOException {
        // 创建Document
        Document doc = DocumentHelper.createDocument();
        // 创建根元素
        Element root = doc.addElement("doc");
        Element pathElement = root.addElement("path");
        String path = "Z:\\";
        // 如果是tif png jpg 的原始图像格式，那么就使用原始图像。如果不是，就使用转化图像
        if (this.sourceImagePath.endsWith(".tif") || this.sourceImagePath.endsWith(".png") || this.sourceImagePath.endsWith(".jpg")){
            path += this.sourceImagePath;
        }else {
            path += this.transferImagePath;
        }
//        root.addAttribute("path",path);

        pathElement.addText(path);
        Element outputsElement = root.addElement("outputs");
        Element objectElement = outputsElement.addElement("object");
        for (DamageData damageData : this.damageDataList){
            Element itemElement = objectElement.addElement("item");
            Element nameElement = itemElement.addElement("name");
            nameElement.addText(String.valueOf(damageData.getDamageType()));

            Element bndboxElement = itemElement.addElement("bndbox");

            Element xminElement = bndboxElement.addElement("xmin");
            xminElement.addText(String.valueOf(damageData.getX_min()));

            Element yminElement = bndboxElement.addElement("ymin");
            yminElement.addText(String.valueOf(damageData.getY_min()));

            Element xmaxElement = bndboxElement.addElement("xmax");
            xmaxElement.addText(String.valueOf(damageData.getX_max()));

            Element ymaxElement = bndboxElement.addElement("ymax");
            ymaxElement.addText(String.valueOf(damageData.getY_max()));

        }

        Element sizeElement = root.addElement("size");
        Element widthElement = sizeElement.addElement("width");
        widthElement.addText(String.valueOf(this.width));
        Element heightElement = sizeElement.addElement("height");
        heightElement.addText(String.valueOf(this.height));
//        sizeElement.addAttribute("width",String.valueOf(this.width));
//        sizeElement.addAttribute("height",String.valueOf(this.height));

        File f = new File(path);
        String fname = f.getName();
//        int x = fname.lastIndexOf(".");
        String caselsh = fname.substring(0,fname.lastIndexOf("."));
//        System.out.println(caselsh);
        String xmlFilePath = floderPath+File.separator+caselsh+"_.xml";
        // 创建输出流
        Writer out = null;

        out = new PrintWriter(xmlFilePath, "utf-8");

        // 格式化
        OutputFormat format = new OutputFormat("\t", true);
        format.setTrimText(true);//去掉原来的空白(\t和换行和空格)！

        XMLWriter writer = new XMLWriter(out, format);
        // 把document对象写到out流中。
        writer.write(doc);
        out.close();
        writer.close();

    }


}
