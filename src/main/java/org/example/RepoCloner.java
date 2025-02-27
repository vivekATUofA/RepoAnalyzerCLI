package org.example;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;

public class RepoCloner {
    public static void cloneRepository(String repoUrl, File repoDir) {
        try {
            System.out.println("Cloning repository from: " + repoUrl);
            Git.cloneRepository()
                    .setURI(repoUrl)
                    .setDirectory(repoDir)
                    .call();
            System.out.println("Repository cloned successfully.");
        } catch (GitAPIException e) {
            System.err.println("Error cloning repository: " + e.getMessage());
            System.exit(1);
        }
    }
}
