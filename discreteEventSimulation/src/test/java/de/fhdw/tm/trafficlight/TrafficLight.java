package de.fhdw.tm.trafficlight;

import java.util.LinkedList;
import java.util.NoSuchElementException;

import de.fhdw.tm.des.modelling.ModelProcess;
import de.fhdw.tm.des.modelling.ProcessStep;
import de.fhdw.tm.des.modelling.ProcessStepDelay;
import de.fhdw.tm.des.scheduler.DESScheduler;

public class TrafficLight {

	private LinkedList<Vehicle> waitingVehicles;
	private Integer id;
	private long greenUntil, timeleft;
	private String start;

	public TrafficLight(Integer id) {
		this.waitingVehicles = new LinkedList<Vehicle>();
		this.id = id;
	}

	public void prepareGreenPhase(long greenUntil) {
		this.greenUntil = greenUntil;
		this.timeleft = this.greenUntil - DESScheduler.getSimulationTime();
		this.start = this.toString();
	}

	public void prepareGreenPhase(long greenUntil, Integer slowStartDelay) {
		this.prepareGreenPhase(greenUntil);
		if (this.waitingVehicles.size() > 0) {
			this.waitingVehicles.getFirst().leavingTime = (int) Math.min(greenUntil - DESScheduler.getSimulationTime(),
					slowStartDelay);
		}
	}

	public void pushVehicle(Vehicle vehicle) {
		this.waitingVehicles.addLast(vehicle);
	}

	@ProcessStepDelay(0)
	public long popVehicleDelay() {
		return 0; // doesnt get called -> see implementation modelling
	}

	@ProcessStep(0)
	public void popVehicle() {
		this.timeleft = this.greenUntil - DESScheduler.getSimulationTime();
		if (timeleft > 0) {
			try {
				Vehicle next = this.waitingVehicles.getFirst();
				if (next.leavingTime <= timeleft) {
					this.waitingVehicles.removeFirst();
					// first vehicle leaving
					DESScheduler.scheduleToFuture(new ModelProcess(this), next.leavingTime);
				}
			} catch (NoSuchElementException e) {
				// no vehicle waiting
				DESScheduler.scheduleToFuture(new ModelProcess(this), 1);
			}
		} else
			DESScheduler.log(this.start + " -> " + this.toString());
	}

	@Override
	public String toString() {
		return DESScheduler.getSimulationTime() + ": " + "Light = " + this.id + ", waiting cars = "
				+ this.waitingVehicles.size();
	}
}
