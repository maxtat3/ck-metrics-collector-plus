package com.github.shellhub.filemanager.event;

import com.github.shellhub.filemanager.entity.FileEntity;

import lombok.Data;

@Data
public class FileEntityEvent {
    private FileEntity fileEntity;
    private int position;

    public FileEntityEvent(FileEntity fileEntity, int position) {
        this.fileEntity = fileEntity;
        this.position = position;
    }
}
