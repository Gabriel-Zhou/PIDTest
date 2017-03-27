package test;

import java.io.File;
import java.security.PrivateKey;

import net.handle.hdllib.*;

public class test {
	public static void main(String[] args) throws Exception {

		// Sample Handle Identifier Resolve Code Snippet
		System.out.println("...Begin Resolving PID test...");
		// Get the UTF8 encoding of the desired handle.
		String resolve_handle = "11723/SENSOR1/412B0708-49E8-4913-A047-D62CEE345811";
		byte someHandle[] = Util.encodeString(resolve_handle);
		// Create a resolution request.
		// (without specifying any types, indexes, or authentication info)

		ResolutionRequest request = new ResolutionRequest(someHandle, null, null, null);
		HandleResolver resolver = new HandleResolver();
		// Create a resolver that will send the request and return the response.
		AbstractResponse response = resolver.processRequest(request);
		// Check the response to see if the operation was successful.
		if (response.responseCode == AbstractMessage.RC_SUCCESS) {
			// The resolution was successful, so we'll cast the response
			// and get the handle values.
			HandleValue values[] = ((ResolutionResponse) response).getHandleValues();
			for (int i = 0; i < values.length; i++) {
				if (String.valueOf(values[i]).contains("index"))
					System.out.println(String.valueOf(values[i]));
			}
		} else {
			System.out.println(response.responseCode);
		}

		// Sample Handle Identifier Create/Assign Code Snippet
		System.out.println("...Begin Assigning PID test...");

		// Read admpriv.bin for Handle instance authentication
		String handle_admin_identifier = "0.NA/11723";
		String admin_privKey_file = "/Users/quzhou/hs/admpriv.bin";

		File privKeyFile = new File(admin_privKey_file);
		PrivateKey hdl_adm_priv = net.handle.hdllib.Util.getPrivateKeyFromFileWithPassphrase(privKeyFile, null);
		byte adm_handle[] = Util.encodeString(handle_admin_identifier);
		AuthenticationInfo auth = new net.handle.hdllib.PublicKeyAuthenticationInfo(adm_handle, 300, hdl_adm_priv);

		System.out.println(
				"Check LHS Admin Authenticaton Status:" + new net.handle.hdllib.Resolver().checkAuthentication(auth));

		// Create one sample Handle identifier
		String handle_url = "http://www.google.com";
		String handle_identifier = "11723/test";
		HandleValue new_value = new HandleValue(1, Util.encodeString("URL"), Util.encodeString(handle_url));
		HandleValue[] new_values = new HandleValue[1];
		new_values[0] = new_value;
		CreateHandleRequest assign_request = new CreateHandleRequest(Util.encodeString(handle_identifier), new_values,
				auth);

		// Return PID create/assign response - one Handle identifier
		AbstractResponse response_assign = resolver.processRequestGlobally(assign_request);
		if (response_assign.responseCode == AbstractMessage.RC_SUCCESS) {
			// The resolution was successful, so we'll cast the handle
			// identifier
			System.out.println("Assigned Persistent Identifier:" + handle_identifier);

		} else if (response_assign.responseCode == AbstractMessage.RC_ERROR) {
			byte values[] = ((ErrorResponse) response_assign).message;
			for (int i = 0; i < values.length; i++) {
				System.out.print(String.valueOf(values[i]));
			}
		}
	}
}