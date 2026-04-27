package edu.advising.common;

public interface PipelineHandler<T> {
    PipelineResult handle(T context);
}
