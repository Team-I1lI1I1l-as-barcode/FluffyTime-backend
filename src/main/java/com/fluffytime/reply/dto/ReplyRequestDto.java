package com.fluffytime.reply.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReplyRequestDto {

    private Long commentId;
    private Long userId;

    @NotBlank(message = "내용 입력은 필수입니다.")
    private String content;
}
