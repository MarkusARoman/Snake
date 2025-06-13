package com.lurch;

import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;

public class KeyInput 
{
    private final long windowHandle;
    private final Set<Integer> pressedKeys = new HashSet<>();
    private final Set<Integer> justPressedKeys = new HashSet<>();
    private final Set<Integer> justReleasedKeys = new HashSet<>();

    public KeyInput(long windowHandle) 
    {
        this.windowHandle = windowHandle;
    }

    public void update() 
    {
        justPressedKeys.clear();
        justReleasedKeys.clear();

        for (int key = GLFW.GLFW_KEY_SPACE; key <= GLFW.GLFW_KEY_LAST; key++) 
        {
            boolean isDown = GLFW.glfwGetKey(windowHandle, key) == GLFW.GLFW_PRESS;
            boolean wasDown = pressedKeys.contains(key);

            if (isDown && !wasDown) {
                justPressedKeys.add(key);
                pressedKeys.add(key);
            } else if (!isDown && wasDown) {
                justReleasedKeys.add(key);
                pressedKeys.remove(key);
            }
        }
    }

    public boolean isKeyDown(int key) 
    {
        return pressedKeys.contains(key);
    }

    public boolean isKeyPressed(int key) 
{
        return justPressedKeys.contains(key);
    }

    public boolean isKeyReleased(int key) 
{
        return justReleasedKeys.contains(key);
    }
}
