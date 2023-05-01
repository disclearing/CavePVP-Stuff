package net.valorhcf;

import net.minecraft.server.NBTCompressedStreamTools;
import net.minecraft.server.NBTTagCompound;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PlayerDataSaveJob implements Runnable {

    private File target;
    private NBTTagCompound data;

    public PlayerDataSaveJob (File target, NBTTagCompound data) {
        this.target = target;
        this.data = data;
    }

    @Override
    public void run() {
        File temp = new File(this.target.getPath() + ".tmp");
        FileOutputStream fileoutputstream = null;
        try {
            fileoutputstream = new FileOutputStream(temp);
            NBTCompressedStreamTools.a(this.data, (OutputStream) fileoutputstream);
            if (this.target.exists()) {
                this.target.delete();
            }
            temp.renameTo(this.target);
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        } finally {
            if(fileoutputstream != null) {
                try {
                    fileoutputstream.close();
                } catch (IOException e) { }
            }
        }
        this.target = null;
        this.data = null;
    }
}
