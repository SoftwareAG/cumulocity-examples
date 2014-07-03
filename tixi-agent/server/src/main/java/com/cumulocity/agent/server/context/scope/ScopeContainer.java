package com.cumulocity.agent.server.context.scope;

import java.util.Set;

/**
 * Defines a scope container.
 * @author Darek Kaczyński
 */
public interface ScopeContainer {

    /**
     * Gets the names of all beans within the container.
     * @return the list of contained bean names.
     * @throws IllegalStateException if the scope container is currently in destruction.
     */
    Set<String> getObjectNames();

    /**
     * Checks if a bean of given name is present within the container.
     * @param name the name of the bean to check.
     * @return true if the bean was found, false otherwise.
     * @throws IllegalStateException if the whole container is currently in destruction.
     */
    boolean contains(String name);

    /**
     * Gets the given bean by name.
     * @param name the name of the bean to retrieve.
     * @return the bean of given name, or <code>null</code> if was not contained.
     * @throws IllegalStateException if object of given name or the whole container is currently in destruction.
     */
    Object getObject(String name);

    /**
     * Puts a bean of given name into the container.
     * @param name the bean name to put.
     * @param obj the bean instance to put.
     * @throws IllegalArgumentException if an object for given name is already present in the container.
     * @throws IllegalStateException if object of given name or the whole container is currently in destruction.
     */
    void putObject(String name, Object obj);

    /**
     * Removes the bean of given name from the container.
     * @param name the name of the bean to remove.
     * @return the removed bean, or <code>null</code> if it was not contained.
     * @throws IllegalStateException if object of given name or the whole container is currently in destruction.
     */
    Object removeObject(String name);

    /**
     * Adds a detruction callback for the bean of given name. All added callbacks will be run upon bean removal from the container.
     * @param name the name of the bean to register callback for.
     * @param callback the destrution callback to register.
     * @throws IllegalStateException if object of given name or the whole container is currently in destruction.
     */
    void addDestructionCallback(String name, Runnable callback);

    /**
     * Clears the container destroying all contained beans.
     * @throws IllegalStateException if the scope container is already during destruction.
     */
    void clear();
}
