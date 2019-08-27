/*
 * IRI.java
 * ePub3
 *
 * Created by Pedro Reis Colaco (txtr) on 2013-08-13.
 */
//  Copyright (c) 2014 Readium Foundation and/or its licensees. All rights reserved.
//  
//  Redistribution and use in source and binary forms, with or without modification, 
//  are permitted provided that the following conditions are met:
//  1. Redistributions of source code must retain the above copyright notice, this 
//  list of conditions and the following disclaimer.
//  2. Redistributions in binary form must reproduce the above copyright notice, 
//  this list of conditions and the following disclaimer in the documentation and/or 
//  other materials provided with the distribution.
//  3. Neither the name of the organization nor the names of its contributors may be 
//  used to endorse or promote products derived from this software without specific 
//  prior written permission.
//  
//  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
//  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
//  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
//  IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
//  INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
//  BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
//  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
//  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
//  OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED 
//  OF THE POSSIBILITY OF SUCH DAMAGE.



package org.readium.sdk.android;

import android.util.Log;
import android.util.Pair;

/**
 * The IRI class encapsulates all URL and URN storage in Readium.
 * 
 * The EPUB 3 specification calls for IRIs rather than URIs (i.e. Unicode characters
 * are allowed and should not be implicitly encoded) in matching properties and other
 * identifiers. This class provides URN support internally, URL support through
 * Google's GURL library, and Unicode IRI support is wrapped around GURL.
 */
public class IRI {
	
	/**
	 * Log tag
	 */
    protected static final String TAG = IRI.class.getName();
    
	/**
	 *  The IRI scheme used to refer to EPUB 3 documents.
	 */
	public static final String EPUBScheme = "epub3";
	

    /**
     * Native IRI Pointer.
     * DO NOT USE FROM JAVA SIDE!
     */
	private final long __nativePtr;
	
	/**
	 * Know when we were destroyed already.
	 */
	private boolean mDestroyed = false;
	
    
    /**
     * Initializes an empty (and thus invalid) IRI.
     */
    public IRI() {
    	// Create the native representation
    	__nativePtr = createNativeIRIempty();
    }
    
    /**
     Create a new IRI.
     @param iriStr A valid URL or IRI string.
     */
    public IRI(String iriStr) {
    	// Create the native representation
    	__nativePtr = createNativeIRIstring(iriStr);
    }
    
    /**
     * Create a URN.
     * 
     * Follows the form 'urn:`nameID`:`namespacedString`'.
     * @param nameID The identifier/namespace for the resource name.
     * @param namespacedString The resource name.
     */
    public IRI(String nameID, String namespacedString) {
    	// Create the native representation
    	__nativePtr = createNativeIRIurn(nameID, namespacedString);
    }
    
	/**
     * Create a simple URL.
     * 
     * The URL will be in the format:
     * 
     *     [scheme]://[host][path]?[query]#[fragment]
     * 
     * If the path is empty or does not begin with a path separator character (`/`),
     * one will be inserted automatically.
     * @param scheme The URL scheme.
     * @param host The host part of the URL.
     * @param path The URL's path.
     * @param query Any query components of the URL, properly URL-encoded.
     * @param fragment A fragment used to identify a particular location within a
     * resource.
     */
    public IRI(String scheme, String host, String path, String query, String fragment) {
    	// Create the native representation
    	__nativePtr = createNativeIRIurl(scheme, host, path, query, fragment);
    }
    public IRI(String scheme, String host, String path, String query) {
    	this(scheme, host, path, query, "");
    }
    public IRI(String scheme, String host, String path) {
    	this(scheme, host, path, "", "");
    }
    
	/**
	 * Destroys this IRI and releases any data of it.
	 */
	public void destroy() {
		if(!mDestroyed) {
	    	// Log destroy
	        Log.d(TAG, "Destroying IRI [ptr:" + String.format("%X", __nativePtr) + "]");
	    	
			// Release the native IRI
			EPub3.releaseNativePointer(__nativePtr);
		} else {
			// Log error
			Log.e(TAG, "Destroying already destroyed IRI [ptr:" + String.format("%X", __nativePtr) + "]");
		}
	}
	
	@Override
	protected void finalize() {
		// If we are not destroyed yet?
		if(!mDestroyed) {
			destroy();
		}
	}
	
    
    /*
	 * Methods to be used from native code
	 */
	
    /**
     * IRI creator method. Just to be used by ePub3 native code.
     * @return Created IRI object.
     */
	private static IRI createIRIempty() {
    	IRI iri = new IRI();
    	
    	return iri;
    }

	/**
     * IRI creator method. Just to be used by ePub3 native code.
     * @param iriStr The IRI string.
     * @return Created IRI object.
     */
	private static IRI createIRIstring(String iriStr) {
    	IRI iri = new IRI(iriStr);
    	
    	return iri;
    }
	
    /**
     * IRI creator method. Just to be used by ePub3 native code.
     * @param nameID The identifier/namespace for the resource name.
     * @param namespacedString The resource name.
     * @return Created IRI object.
     */
	private static IRI createIRIurn(String nameID, String namespacedString) {
    	IRI iri = new IRI(nameID, namespacedString);
    	
    	return iri;
    }
	
    /**
     * IRI creator method. Just to be used by ePub3 native code.
     * @param scheme The URL scheme.
     * @param host The host part of the URL.
     * @param path The URL's path.
     * @param query Any query components of the URL, properly URL-encoded.
     * @param fragment A fragment used to identify a particular location within a
     * resource.
     * @return Created IRI object.
     */
	private static IRI createIRIurl(String scheme, String host, String path, String query, String fragment) {
    	IRI iri = new IRI(scheme, host, path, query, fragment);
    	
    	return iri;
    }


    /*
     * Private native methods
     */

	/**
     * Creates an empty native IRI object. 
     * THIS SHOULD ONLY BE USED INSIDE THE CONSTRUCTOR.
     * @return The pointer to the native object.
     */
    private native long createNativeIRIempty();
    
    /**
     * Creates a native IRI object from IRI string.
     * THIS SHOULD ONLY BE USED INSIDE THE CONSTRUCTOR.
     * @param iriStr The IRI string.
     * @return The pointer to the native object.
     */
    private native long createNativeIRIstring(String iriStr);
    
    /**
     * Creates a native IRI object as URN. 
     * THIS SHOULD ONLY BE USED INSIDE THE CONSTRUCTOR.
     * @param scheme The URL scheme.
     * @param host The host part of the URL.
     * @param path The URL's path.
     * @param query Any query components of the URL, properly URL-encoded.
     * @param fragment A fragment used to identify a particular location within a
     * resource.
     * @return The pointer to the native object.
     */
    private native long createNativeIRIurn(String nameID, String namespacedString);
    
    /**
     * Creates a native IRI object as URL. 
     * THIS SHOULD ONLY BE USED INSIDE THE CONSTRUCTOR.
     * @param scheme The URL scheme.
     * @param host The host part of the URL.
     * @param path The URL's path.
     * @param query Any query components of the URL, properly URL-encoded.
     * @param fragment A fragment used to identify a particular location within a
     * resource.
     * @return The pointer to the native object.
     */
    private native long createNativeIRIurl(String scheme, String host, String path, String query, String fragment);
    

    /*
     * Public native methods
     */
    
    /**
     * Checks if this IRI is a URN.
     * @return True if the IRI is a URN.
     */
    public native boolean isURN();
    
    /**
     * Checks if the IRI is a URL referencing a relative location.
     * @return True if referencing a relative location.
     */
    public native boolean isRelative();
    
    /**
     * Checks if the IRI is empty.
     * @return True if the IRI is empty.
     */
    public native boolean isEmpty();
    
    /**
     * Obtains the IRI's scheme component.
     * @returns String with scheme component.
     */
    public native String getScheme();

    /**
     * Obtains the name-id component of a URN IRI.
     * @return The name-id component String.
     */
    public native String getNameID() throws IllegalArgumentException;
    
    /**
     * Retrieves the host component of a URL IRI.
     * @return The host component String.
     */
    public native String getHost();
    
    /**
     * Retrieves any credentials attached to an IRI.
     * @return An account and shared-secret in a Pair<String, String>, such as username and password.
     */
    public Pair<String, String>  getCredentials() {
    	String uid = getCredentialsUID();
    	String pwd = getCredentialsPWD();
    	return new Pair<String,String>(uid, pwd);
    }
    private native String getCredentialsUID();
    private native String getCredentialsPWD();
    
    /**
     * Gets the namespace-qualified part of an URN IRI.
     * @return The namespace-qualified part string.
     */
    public native String getNamespacedString() throws IllegalArgumentException;
    
    /**
     * Obtains the port number associated with a URL IRI.
     * @return The port number.
     */
    public native int getPort();
    
    /**
     * Obtains the path component of a URL IRI.
     * @param URLEncoded If `true`, returns the path in URL-encoded format. Otherwise,
     * the path will be decoded first, yielding a standard POSIX file-system path.
     */
    public native String getPath(boolean URLEncoded);
    public String getPath() {
    	return getPath(true);
    }
    
    /**
     * Retrieves the query portion of a URL IRI, if any.
     * @return The query string or empty string if none.
     */
    public native String getQuery();
    
    /**
     * Retrieves any fragment part of a URL IRI.
     * @return The fragment string or empty string if none.
     */
    public native String getFragment();
    
    /**
     * Obtains the last path component of a URL IRI.
     * @return The last path component string.
     */
    public native String getLastPathComponent();
    
    /**
     * Returns any CFI present in a URL IRI.
     * 
     * If the fragment part of a URL is a valid Content Fragment Identifier (i.e. if
     * the URL's fragment begins with `epubcfi(`) then this will parse the fragment
     * into a CFI representation.
     * @return A valid CFI if one is present in the URL's fragment, or an empty CFI if
     * no content fragment identifier is present.
     */
    public native CFI getContentFragmentIdentifier();
    
    /**
     * Assigns a scheme to this IRI.
     * @param scheme The new scheme.
     */
    public native void setScheme(String scheme);
    
    /**
     * Assigns a host to this IRI.
     * @param host The new host component.
     */
    public native void setHost(String host);
    
    /**
     * Sets credentials for this IRI.
     * @param user The username for the credential.
     * @param pass The shared-secret part of the credential.
     */
    public native void setCredentials(String user, String pass);
    
    /**
     * Appends a new component to a URL IRI's path.
     * @param component The new path component.
     */
    public native void addPathComponent(String component);
    
    /**
     * Adds or replaces the query component of a URL IRI.
     * @param query The new query String.
     */
    public native void setQuery(String query);
    
    /**
     * Adds or replaces the fragment component of a URL IRI.
     * @param fragment The new fragment String.
     */
    public native void setFragment(String fragment);
    
    /**
     * Sets a URL IRI's fragment using a Content Fragment Identifier.
     */
    public native void setContentFragmentIdentifier(CFI cfi);
    
    /**
     * Obtains a Unicode String representation of this IRI.
     * 
     * Only percent-encodes URL-reserved characters within components, and uses IDN
     * algorithm to obtain a Unicode hostname.
     * 
     * Note that any components which are already URL-encoded will not be explicitly
     * decoded by this function.
     * @result A Unicode IRI String.
     */
    public native String toIRIString();
    
    /**
     * Obtains a valid ASCII URL representation of this IRI.
     * 
     * Percent-encodes all URL-reserved characters and all non-ASCII characters outside
     * the hostname using those characters' UTF-8 representations. Uses IDN algorithm to
     * obtain an ASCII representation of any Unicode hostname.
     * @result An ASCII URL String; even though the result is a Unicode String type, the
     * characters are guaranteed to be valid ASCII suitable for use with non-Unicode
     * libraries.
     */
    public native String toURIString();
    
    /**
     * Returns a Unicode String representation of this IRI.
     * This is the default cast to String for IRI. If you want to have a URI string use
     * toURIString().
     * @see Object#toString()
     */
    @Override
    public String toString() {
    	if(isURN()) {
    		return toIRIString();
    	} else {
    		return toURIString();
    	}
    }
    

    /*
     * Static native helper methods
     */
    
    /**
     * URL-encodes a path, query, or fragment component.
     * @param str String to be encoded.
     * @return The encoded string.
     */
    public static native String URLEncodeComponent(String str);
    
    /**
     * Percent-encodes the UTF-8 representation of any non-ASCII characters in a String.
     * @param str String to be encoded.
     * @return The encoded string.
     */
    public static native String PercentEncodeUCS(String str);
    
    /**
     * Converts an IDN (hostname in non-ASCII Unicode format) into its ASCII representation.
     * @param host IDN string to be encoded.
     * @return The encoded string.
     */
    public static native String IDNEncodeHostname(String host);
    
}
