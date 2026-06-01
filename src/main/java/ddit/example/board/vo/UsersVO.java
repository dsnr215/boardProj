package ddit.example.board.vo;

import lombok.Data;

@Data
public class UsersVO
{
	private String userId;
    private String password;
    private String name;
    private String role;
    private int enabled;
}
