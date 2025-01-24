package com.alinesno.infra.smart.assistant.template.utils;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

import java.io.File;

/**
 * Gitlab工具类
 * @author luoxiaodong
 * @since 1.0.0
 */
public class GitUtils {

	/**
	 * clone 工程
	 * @param uri
	 * @param path
	 */
	public static void clone(String uri , String path) {
        try {
            Git.cloneRepository()
                .setURI(uri)
                .setDirectory(new File(path))
                .call();
        } catch (InvalidRemoteException e) {
            System.err.println("Invalid remote repository: " + e.getMessage());
        } catch (TransportException e) {
            System.err.println("Error occurred during transport: " + e.getMessage());
        } catch (GitAPIException e) {
            System.err.println("Git API error: " + e.getMessage());
        }
    }
	
}
