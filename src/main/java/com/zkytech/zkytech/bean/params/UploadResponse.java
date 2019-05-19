package com.zkytech.zkytech.bean.params;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadResponse {
    @NonNull
    private String url;

    @NonNull
    private String name;

    @NonNull
    private String status;

    private String thumbUrl;
}
