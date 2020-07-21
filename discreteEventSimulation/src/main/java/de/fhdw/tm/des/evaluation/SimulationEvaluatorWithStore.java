package de.fhdw.tm.des.evaluation;

import java.util.Properties;

import org.apache.commons.math3.util.ResizableDoubleArray;

import de.fhdw.tm.des.evaluation.aggregation.EvaluationPersistantCharacteristic;

public abstract class SimulationEvaluatorWithStore extends SimulationEvaluator {
	
	private EvaluationPersistantCharacteristic[] characteristic;
	private ResizableDoubleArray data;

	public SimulationEvaluatorWithStore(String name, Object sut, EvaluationPersistantCharacteristic... characteristic) {
		super(name, sut);
		this.characteristic = characteristic;
		this.data = new ResizableDoubleArray();
	}

	@Override
	public Properties eval() {
		Properties result = new Properties();
		for (EvaluationPersistantCharacteristic c : this.characteristic) {
			result.put(c.getDesciption(), c.eval(this.data));
		}
		return result;
	}
	
	public void addData(double value) {
		this.data.addElement(value);
	}
	
	public void addData(int value) {
		this.data.addElement(value);
	}
	
	public void addData(long value) {
		this.data.addElement(value);
	}

}
