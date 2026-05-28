package ddit.example.board.vo;

import java.sql.Date;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BoardVO {

	private int brdNo;

	@NotBlank(message = "빈 값 금지")
	@Size(min = 2, max = 50, message = "최소 2자 ~ 최대 50자 제한")
	private String title;

	@NotBlank(message = "빈 값 금지")
	private String content;

	private String writerId;

	private Date regDate;
}