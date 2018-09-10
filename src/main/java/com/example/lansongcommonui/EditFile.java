package com.example.lansongcommonui;

import android.util.Log;
import com.lansosdk.videoeditor.LanSongFileUtil;

public class EditFile {
    private static EditFile mInstance;
    public String bgMusicPath;
    private String editedVideo = null;
    private String originalVideo = null;

    public static synchronized EditFile getInstance() {
        EditFile editFile;
        synchronized (EditFile.class) {
            if (mInstance == null) {
                mInstance = new EditFile();
            }
            editFile = mInstance;
        }
        return editFile;
    }

    public void initEditVideo(String str) {
        this.originalVideo = str;
        this.editedVideo = str;
    }

    public String saveEditedVideo(String str) {
        if (LanSongFileUtil.fileExist(str)) {
            LanSongFileUtil.deleteFile(this.editedVideo);
            this.editedVideo = str;
        } else {
            Log.e("editFile", "set Edited video error");
        }
        return this.editedVideo;
    }

    public String getEditedVideo() {
        if (LanSongFileUtil.fileExist(this.editedVideo)) {
            return this.editedVideo;
        }
        return LanSongFileUtil.fileExist(this.originalVideo) ? this.originalVideo : null;
    }

    public String getOriginalVideo() {
        return this.originalVideo;
    }
}
