package com.cloudj.backend.domain.model;

public class FileChunk {

    private String fileId;
    private String chunkIndex;

    public FileChunk(String fileId, String chunkIndex) {
        this.fileId = fileId;
        this.chunkIndex = chunkIndex;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getChunkIndex() {
        return chunkIndex;
    }

    public void setChunkIndex(String chunkIndex) {
        this.chunkIndex = chunkIndex;
    }
}
