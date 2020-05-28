package kd.dw.form.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import kd.bos.algo.DataType;

public class MyDataType extends DataType {
	
	private DataType dataType =null;
	
	
	public DataType coverDataTypeFromObj() {
		return dataType;
	}

	protected MyDataType(int ordinal) {
		super(ordinal);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean acceptsType(DataType arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getFixedSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Class<?> getJavaType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getSqlType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object read(DataInputStream arg0) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void write(Object arg0, DataOutputStream arg1) throws IOException {
		// TODO Auto-generated method stub

	}

}
