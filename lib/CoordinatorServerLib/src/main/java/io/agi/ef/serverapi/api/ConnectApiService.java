package io.agi.ef.serverapi.api;

import io.agi.ef.serverapi.api.*;
import io.agi.ef.serverapi.model.*;

import com.sun.jersey.multipart.FormDataParam;


import java.util.List;
import io.agi.ef.serverapi.api.NotFoundException;

import java.io.InputStream;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import javax.ws.rs.core.Response;

public abstract class ConnectApiService {
  
      public abstract Response connectHostHostPortPortContextPathContextPathGet(String host,String port,String contextPath)
      throws NotFoundException;
  
}
