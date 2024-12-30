package com.example.demo.dto.common;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageSerializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter @Setter
public class EPageSerializable <T> implements Serializable {
	private static final long serialVersionUID = 1L;
	//总记录数
	@Schema(description = "전체 개수", example = "1")
	protected long totalCount;
	//结果集
	@Schema(description = "목록")
	protected List<T> list;

	public EPageSerializable() {
	}

	@SuppressWarnings("unchecked")
	public EPageSerializable(List<? extends T> list) {
		this.list = (List<T>) list;
		if (list instanceof Page) {
			this.totalCount = ((Page<?>)list).getTotal();
		} else {
			totalCount = list.size();
		}
	}

	public static <T> PageSerializable<T> of(List<? extends T> list) {
		return new PageSerializable<T>(list);
	}

	@Override
	public String toString() {
		return "PageSerializable{" +
				"totalCount=" + totalCount +
				", list=" + list +
				'}';
	}
}

