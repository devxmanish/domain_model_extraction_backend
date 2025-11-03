package com.devxmanish.DomainModelExtraction.enums;

public enum Status {
    PENDING, // job created but not processed
    PROCESSED, // job processed initially but not consolidated yet
    CONFIRMED, // confirmed after the consolidation and final model is ready for uml generation
    UMLGENERATED,
    FAILED
}
