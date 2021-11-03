package ru.wert.datapik.client.entity.api_interfaces;


import retrofit2.Call;
import retrofit2.http.*;
import ru.wert.datapik.client.entity.models.AnyPart;
import ru.wert.datapik.client.entity.models.Draft;

import java.util.List;
import java.util.Set;

public interface AnyPartApiInterface {

    @GET("parts/id/{id}")
    Call<AnyPart> getById(@Path("id") Long id);

    @GET("parts/name/{name}")
    Call<AnyPart> getByName(@Path("name") String name);

    @GET("parts/all")
    Call<List<AnyPart>> getAll();

    @GET("parts/all-by-text/{text}")
    Call<List<AnyPart>> getAllByText(@Path("text") String text);

    @POST("parts/create")
    Call<AnyPart> create(@Body AnyPart entity);

    @PUT("parts/update")
    Call<Void> update(@Body AnyPart entity);

    @DELETE("parts/delete/{id}")
    Call<Void> deleteById(@Path("id") Long id);
    
    //ЧЕРТЕЖИ

//    @GET("parts/drafts-in-part/{partId}")
//    Call<Set<Draft>> getDraft(@Path("partId") Long id);
//
//    @GET("parts/add-draft-in-part/{partId}/{draftId}")
//    Call<Set<Draft>> addDraft(@Path("partId") Long partId, @Path("draftId") Long draftId);
//
//    @GET("parts/remove-draft-in-part/{partId}/{draftId}")
//    Call<Set<Draft>> removeDraft(@Path("partId") Long partId, @Path("draftId") Long draftId);

}
