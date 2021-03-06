package controle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.commons.codec.digest.DigestUtils;

import utils.FileUtil;
import utils.ImageTools;
import utils.XmlTools;
import utils.ZipFileTools;

public class DealTheme implements ActionListener{
    private JTextField sourcePathField;
    private JTextArea resultArea;
    
    public DealTheme(JTextField sourcePathField,JTextArea resultArea){
        this.sourcePathField = sourcePathField;
        this.resultArea = resultArea;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ZipFileTools zipFileTool = new ZipFileTools();
        ImageTools imageTool = new ImageTools();
        String zipFilePath,unZipFilePath,savePath;
        
        zipFilePath = sourcePathField.getText();
        
        resultArea.append(zipFilePath+"\n");
        
        unZipFilePath = zipFilePath.substring(0, zipFilePath.lastIndexOf("."));
        
        resultArea.append("开始解压主题文件\n");
        zipFileTool.unZip(zipFilePath, 0);
        resultArea.append("解压主题文件完成\n");
        
        resultArea.append("开始修改主题信息\n");
        XmlTools xmlTools = new XmlTools(unZipFilePath+"/description.xml");
        Map<String,String> map = new HashMap<String,String>();
        map.put("designer", "LHY");
        map.put("author", "LHY");
        xmlTools.update(map);
        resultArea.append("修改主题信息完成\n");
        
        resultArea.append("开始修改壁纸信息\n");
        File wallPaperDir = new File(unZipFilePath+"/wallpaper");
        File[] fileList = wallPaperDir.listFiles();
        
        for(int i=0;i<fileList.length;i++){
            InputStream in;
            try {
                in = new FileInputStream(fileList[i].getPath());
                resultArea.append("原壁纸文件MD5为:"+DigestUtils.md5Hex(in)+"\n");
                in.close();
                imageTool.changeExifMetadata(fileList[i].getPath());
                
                in = new FileInputStream(fileList[i].getPath());
                resultArea.append("修改后壁纸文件MD5为:"+DigestUtils.md5Hex(in)+"\n");
                in.close();
            } catch (FileNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            
            
        }
        resultArea.append("修改壁纸信息完成\n");
        
        zipFilePath = zipFilePath.replace("\\", "/");
        savePath = zipFilePath.substring(0, zipFilePath.lastIndexOf("/")) + "/out.mtz";
        
        resultArea.append("开始重新打包\n");
        zipFileTool.zipMIUI(savePath, unZipFilePath);
        //压缩完成后 删除解压出来的文件
        FileUtil.delete(unZipFilePath);
        resultArea.append("重新打包完成\n");
        resultArea.append("重新生成的主题文件为:"+savePath);
    }
}
