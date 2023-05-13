package com.rozmer.service.dataobject;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class PratingPostDto {
	private Integer id;
	private Integer rating;
	@DateTimeFormat(pattern = "DD-MM-YYYY")
	private Date addedDate;

	private String takeDown;
	private String comment;
}
