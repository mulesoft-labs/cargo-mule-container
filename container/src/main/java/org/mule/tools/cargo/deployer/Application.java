package org.mule.tools.cargo.deployer;

import java.util.List;

/**
 * Maps application JSON type.
 *
 * @see http://www.mulesoft.org/documentation/display/ION/Get+An+Application
 */
public class Application {

    public enum Status {
        DEPLOYING,
        STARTED,
        UNDEPLOYED,
        DEPLOY_FAILED
    }

    public static class WorkerStatus {

        private String id;
        private String host;
        private int port;
        private Status status;

        public String getId() {
            return this.id;
        }

        public void setId(final String id) {
            this.id = id;
        }

        public String getHost() {
            return this.host;
        }

        public void setHost(final String host) {
            this.host = host;
        }

        public int getPort() {
            return this.port;
        }

        public void setPort(final int port) {
            this.port = port;
        }

        public Status getStatus() {
            return this.status;
        }

        public void setStatus(final Status status) {
            this.status = status;
        }

    }

    private String description;
    private String domain;
    private String fullDomain;
    private Status status;
    private boolean hasFile;
    private int workers;
    private int maxWorkers;
    private List<WorkerStatus> workerStatuses;

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getDomain() {
        return this.domain;
    }

    public void setDomain(final String domain) {
        this.domain = domain;
    }

    public String getFullDomain() {
        return this.fullDomain;
    }

    public void setFullDomain(final String fullDomain) {
        this.fullDomain = fullDomain;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(final Status status) {
        this.status = status;
    }

    public boolean isHasFile() {
        return this.hasFile;
    }

    public void setHasFile(final boolean hasFile) {
        this.hasFile = hasFile;
    }

    public int getWorkers() {
        return this.workers;
    }

    public void setWorkers(final int workers) {
        this.workers = workers;
    }

    public int getMaxWorkers() {
        return this.maxWorkers;
    }

    public void setMaxWorkers(final int maxWorkers) {
        this.maxWorkers = maxWorkers;
    }

    public List<WorkerStatus> getWorkerStatuses() {
        return this.workerStatuses;
    }

    public void setWorkerStatuses(final List<WorkerStatus> workerStatuses) {
        this.workerStatuses = workerStatuses;
    }

}