package com.fluffytime.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "profile_images")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Getter
@Setter
public class ProfileImages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_image_id")
    private Long profileImageId;

    @Column(name = "s3i_file_name", length = 255)
    private String s3iFileName;

    @Column(name = "filename", length = 255, nullable = false)
    private String fileName;

    @Column(name = "filepath", length = 255)
    private String filePath;

    @Column(name = "filesize", columnDefinition = "BIGINT")
    private Long fileSize;

    @Column(name = "mimetype", length = 50)
    private String mimeType;


    @Column(name = "upload_data")
    private LocalDateTime uploadDate;

    @PrePersist
    public void create() {
        this.uploadDate = LocalDateTime.now();
    }
}
