package com.au.hackathon.otp.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import com.fasterxml.jackson.annotation.JsonFormat;

@Embeddable
@Audited
@Data

public class DefaultEntity implements Serializable{

	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "action", length = 20, nullable = false)
	    private String action;

	    @Column(name = "created_by", length = 15, nullable = false)
	    private String createdBy;

//	    @Column(name = "created_time", nullable = false)
//	    private LocalDateTime creationDateTime;

	    @Column(name = "updated_by", length = 15)
	    private String updatedBy;

	    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	    @Column(name = "created_time", nullable = false)
	    private LocalDateTime creationDateTime;

	    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	    @Column(name = "updated_time")
	    private LocalDateTime updatedTime;
    // Getters and Setters

		public String getAction() {
			return action;
		}

		public void setAction(String action) {
			this.action = action;
		}

		public String getCreatedBy() {
			return createdBy;
		}

		public void setCreatedBy(String createdBy) {
			this.createdBy = createdBy;
		}

		public LocalDateTime getCreationDateTime() {
			return creationDateTime;
		}

		public void setCreationDateTime(LocalDateTime creationDateTime) {
			this.creationDateTime = creationDateTime;
		}

		public String getUpdatedBy() {
			return updatedBy;
		}

		public void setUpdatedBy(String updatedBy) {
			this.updatedBy = updatedBy;
		}

		public LocalDateTime getUpdatedTime() {
			return updatedTime;
		}

		public void setUpdatedTime(LocalDateTime updatedTime) {
			this.updatedTime = updatedTime;
		}

		public DefaultEntity(String action, String createdBy, LocalDateTime creationDateTime, String updatedBy,
				LocalDateTime updatedTime) {
			super();
			this.action = action;
			this.createdBy = createdBy;
			this.creationDateTime = creationDateTime;
			this.updatedBy = updatedBy;
			this.updatedTime = updatedTime;
		}

		public DefaultEntity() {
			super();
			// TODO Auto-generated constructor stub
		}

   
    
    
    
}
