package org.ditw.rschr

import org.json4s.{DefaultFormats, FieldSerializer}

/**
  * Created by dev on 2017-11-08.
  */

object OrcidProfile_2015 extends Serializable {
  import org.json4s.FieldSerializer._

  case class OrcidIdentifier(
                              value:String,
                              uri:String,
                              path:String,
                              host:String
                            )

  case class OrcidPreferences(locale:String)

  case class DateInfo(value:Long)

  case class StringInfo(value:String)

  case class BooleanInfo(value:Boolean)

  case class OrcidSource(
                            source_orcid:OrcidIdentifier,
                            source_client_id:OrcidIdentifier,
                            source_name:StringInfo,
                            source_date:DateInfo
                          )

  object Renames_OrcidSource extends Serializable {
    val source_orcid = "source_orcid"
    val source_orcid_orig = "source-orcid"

    val source_client_id = "source_client_id"
    val source_client_id_orig = "source-client-id"

    val source_name = "source_name"
    val source_name_orig = "source-name"

    val source_date = "source_date"
    val source_date_orig = "source-date"

    val renames = FieldSerializer[OrcidSource](
      renameTo(source_orcid, source_orcid_orig) orElse
        renameTo(source_client_id, source_client_id_orig) orElse
        renameTo(source_name, source_name_orig) orElse
        renameTo(source_date, source_date_orig),
      renameFrom(source_orcid_orig, source_orcid) orElse
        renameFrom(source_client_id_orig, source_client_id) orElse
        renameFrom(source_name_orig, source_name) orElse
        renameFrom(source_date_orig, source_date)
    )
  }

  case class OrcidHistory(
                           creation_method:String,
                           completion_date:DateInfo,
                           submission_date:DateInfo,
                           last_modified_date:DateInfo,
                           claimed:BooleanInfo,
                           source:OrcidSource,
                           deactivation_date:DateInfo,
                           verified_email:BooleanInfo,
                           verified_primary_email:BooleanInfo,
                           visibility:String
                         )

  object Renames_OrcidHistory extends Serializable {
    val creation_method = "creation_method"
    val creation_method_orig = "creation-method"

    val submission_date = "submission_date"
    val submission_date_orig = "submission-date"

    val last_modified_date = "last_modified_date"
    val last_modified_date_orig = "last-modified-date"

    val completion_date = "completion_date"
    val completion_date_orig = "completion-date"

    val deactivation_date = "deactivation_date"
    val deactivation_date_orig = "deactivation-date"

    val verified_email = "verified_email"
    val verified_email_orig = "verified-email"

    val verified_primary_email = "verified_primary_email"
    val verified_primary_email_orig = "verified-primary-email"

    val renames = FieldSerializer[OrcidHistory](
      renameTo(creation_method, creation_method_orig) orElse
        renameTo(submission_date, submission_date_orig) orElse
        renameTo(completion_date, completion_date_orig) orElse
        renameTo(last_modified_date, last_modified_date_orig) orElse
        renameTo(deactivation_date, deactivation_date_orig) orElse
        renameTo(verified_email, verified_email_orig) orElse
        renameTo(verified_primary_email, verified_primary_email_orig),
      renameFrom(creation_method_orig, creation_method) orElse
        renameFrom(submission_date_orig, submission_date) orElse
        renameFrom(completion_date_orig, completion_date) orElse
        renameFrom(last_modified_date_orig, last_modified_date) orElse
        renameFrom(deactivation_date_orig, deactivation_date) orElse
        renameFrom(verified_email_orig, verified_email) orElse
        renameFrom(verified_primary_email_orig, verified_primary_email)
    )
  }

  case class OtherNames(
                       names:Array[StringInfo],
                       visibility: String
                       )

  object Renames_OtherNames extends Serializable {
    val names = "names"
    val names_orig = "other-name"

    val renames = FieldSerializer[OtherNames](
      renameTo(names, names_orig),
      renameFrom(names_orig, names)
    )
  }

  case class PersonalDetails(
                            given_names:StringInfo,
                            family_name:StringInfo,
                            credit_name:StringInfo,
                            other_names:OtherNames
                            )

  object Renames_PersonalDetails extends Serializable {
    val given_names = "given_names"
    val given_names_orig = "given-names"

    val family_name = "family_name"
    val family_name_orig = "family-name"

    val credit_name = "credit_name"
    val credit_name_orig = "credit-name"

    val other_names = "other_names"
    val other_names_orig = "other-names"

    val renames = FieldSerializer[PersonalDetails](
      renameTo(given_names, given_names_orig) orElse
        renameTo(family_name, family_name_orig) orElse
        renameTo(credit_name, credit_name_orig) orElse
        renameTo(other_names, other_names_orig),
      renameFrom(given_names_orig, given_names) orElse
        renameFrom(family_name_orig, family_name) orElse
        renameFrom(credit_name_orig, credit_name) orElse
        renameFrom(other_names_orig, other_names)
    )
  }

  case class ExternalIdentifier(
                               orcid:String,
                               eid_orcid:String,
                               eid_common_name:StringInfo,
                               eid_reference:StringInfo,
                               eid_url:StringInfo,
                               eid_source:String,
                               source:OrcidSource
                               )

  object Renames_ExternalIdentifier extends Serializable {
    val eid_orcid = "eid_orcid"
    val eid_orcid_orig = "external-id-orcid"

    val eid_common_name = "eid_common_name"
    val eid_common_name_orig = "external-id-common-name"

    val eid_reference = "eid_reference"
    val eid_reference_orig = "external-id-reference"

    val eid_url = "eid_url"
    val eid_url_orig = "external-id-url"

    val eid_source = "eid_source"
    val eid_source_orig = "external-id-source"

    val renames = FieldSerializer[ExternalIdentifier](
      renameTo(eid_orcid, eid_orcid_orig) orElse
        renameTo(eid_common_name, eid_common_name_orig) orElse
        renameTo(eid_reference, eid_reference_orig) orElse
        renameTo(eid_url, eid_url_orig) orElse
        renameTo(eid_source, eid_source_orig),
      renameFrom(eid_orcid_orig, eid_orcid) orElse
        renameFrom(eid_common_name_orig, eid_common_name) orElse
        renameFrom(eid_reference_orig, eid_reference) orElse
        renameFrom(eid_url_orig, eid_url) orElse
        renameFrom(eid_source_orig, eid_source)
    )
  }

  case class ExternalIdentifiers(
                                eids:Array[ExternalIdentifier],
                                visibility:String
                                )

  object Renames_ExternalIdentifiers extends Serializable {
    val eids = "eids"
    val eids_orig = "external-identifier"

    val renames = FieldSerializer[ExternalIdentifiers](
      renameTo(eids, eids_orig),
      renameFrom(eids_orig, eids)
    )
  }

  case class ContactEmail(
                         value:String,
                         primary:Boolean,
                         current:Boolean,
                         verified:Boolean,
                         visibility:String,
                         source:String,
                         source_client_id:String
                         )

  object Renames_ContactEmail extends Serializable {
    val source_client_id = "source_client_id"
    val source_client_id_orig = "source-client-id"

    val renames = FieldSerializer[ContactEmail](
      renameTo(source_client_id, source_client_id_orig),
      renameFrom(source_client_id_orig, source_client_id)
    )
  }

  case class ContactDetails(
                           email:Array[ContactEmail],
                           address:ContactAddr
                           )

  case class Biography(value:String, visibility:String)

  case class ResearcherUrl(name:StringInfo, url:StringInfo)

  object Renames_ResearcherUrl extends Serializable {
    val name = "name"
    val name_orig = "url-name"

    val renames = FieldSerializer[ResearcherUrl](
      renameTo(name, name_orig),
      renameFrom(name_orig, name)
    )
  }

  case class ResearcherUrls(urls:Array[ResearcherUrl], visibility:String)

  object Renames_ResearcherUrls extends Serializable {
    val urls = "urls"
    val urls_orig = "researcher-url"

    val renames = FieldSerializer[ResearcherUrls](
      renameTo(urls, urls_orig),
      renameFrom(urls_orig, urls)
    )
  }

  case class Keywords(keyword:Array[StringInfo], visibility:String)

  case class Bio(
    person_details:PersonalDetails,
    biography:Biography,
    researcher_urls:ResearcherUrls,
    contact_details:ContactDetails,
    keywords:Keywords,
    external_identifiers:ExternalIdentifiers,
    delegation:String,
    applications:Option[String],
    scope:String
    )

  object Renames_Bio extends Serializable {
    val person_details = "person_details"
    val person_details_orig = "personal-details"

    val researcher_urls = "researcher_urls"
    val researcher_urls_orig = "researcher-urls"

    val contact_details = "contact_details"
    val contact_details_orig = "contact-details"

    val external_identifiers = "external_identifiers"
    val external_identifiers_orig = "external-identifiers"

    val renames = FieldSerializer[Bio](
      renameTo(person_details, person_details_orig) orElse
        renameTo(researcher_urls, researcher_urls_orig) orElse
        renameTo(external_identifiers, external_identifiers_orig) orElse
        renameTo(contact_details, contact_details_orig),
      renameFrom(person_details_orig, person_details) orElse
        renameFrom(researcher_urls_orig, researcher_urls) orElse
        renameFrom(external_identifiers_orig, external_identifiers) orElse
        renameFrom(contact_details_orig, contact_details)
    )
  }

  case class DateYMD(year:StringInfo, month:StringInfo, day:StringInfo)
  case class DateYMD_Media(year:StringInfo, month:StringInfo, day:StringInfo, media_type:StringInfo)

  object Renames_DateYMD_Media extends Serializable {
    val media_type = "media_type"
    val media_type_orig = "media-type"

    val renames = FieldSerializer[DateYMD_Media](
      renameTo(media_type, media_type_orig),
      renameFrom(media_type_orig, media_type)
    )
  }

  case class NameInfo(value:String, visibility:String)

  case class Addr(
                 city:String,
                 region:String,
                 country:String
                 )

  case class ContactAddrCountry(value:String, visibility:String)
  case class ContactAddr(
                   country:ContactAddrCountry
                 )

  case class DisambiguatedOrganization(
    disambiguated_organization_identifier:String,
    disambiguation_source:String)

  object Renames_DisambiguatedOrganization extends Serializable {
    val disambiguated_organization_identifier = "disambiguated_organization_identifier"
    val disambiguated_organization_identifier_orig = "disambiguated-organization-identifier"

    val disambiguation_source = "disambiguation_source"
    val disambiguation_source_orig = "disambiguation-source"

    val renames = FieldSerializer[DisambiguatedOrganization](
      renameTo(disambiguated_organization_identifier, disambiguated_organization_identifier_orig) orElse
        renameTo(disambiguation_source, disambiguation_source_orig),
      renameFrom(disambiguated_organization_identifier_orig, disambiguated_organization_identifier) orElse
        renameFrom(disambiguation_source_orig, disambiguation_source)
    )
  }
  case class Organization(
                         name:String,
                         address:Addr,
                         disambiguated_organization: DisambiguatedOrganization
                         )

  object Renames_Organization extends Serializable {
    val disambiguated_organization = "disambiguated_organization"
    val disambiguated_organization_orig = "disambiguated-organization"

    val renames = FieldSerializer[Organization](
      renameTo(disambiguated_organization, disambiguated_organization_orig),
      renameFrom(disambiguated_organization_orig, disambiguated_organization)
    )
  }

  case class Affiliation(
                        _type:String,
                        department_name:String,
                        role_title:String,
                        start_date:DateYMD,
                        end_date:DateYMD,
                        organization:Organization,
                        source:OrcidSource,
                        created_date:DateInfo,
                        last_modified_date:DateInfo,
                        visibility:String,
                        put_code:String
                        )

  object Renames_Affiliation extends Serializable {
    val _type = "_type"
    val _type_orig = "type"

    val department_name = "department_name"
    val department_name_orig = "department-name"

    val role_title = "role_title"
    val role_title_orig = "role-title"

    val start_date = "start_date"
    val start_date_orig = "start-date"

    val end_date = "end_date"
    val end_date_orig = "end-date"

    val created_date = "created_date"
    val created_date_orig = "created-date"

    val last_modified_date = "last_modified_date"
    val last_modified_date_orig = "last-modified-date"

    val put_code = "put_code"
    val put_code_orig = "put-code"

    val renames = FieldSerializer[Affiliation](
      renameTo(_type, _type_orig) orElse
        renameTo(department_name, department_name_orig) orElse
        renameTo(role_title, role_title_orig) orElse
        renameTo(start_date, start_date_orig) orElse
        renameTo(end_date, end_date_orig) orElse
        renameTo(created_date, created_date_orig) orElse
        renameTo(last_modified_date, last_modified_date_orig) orElse
        renameTo(put_code, put_code_orig),
      renameFrom(_type_orig, _type) orElse
        renameFrom(department_name_orig, department_name) orElse
        renameFrom(role_title_orig, role_title) orElse
        renameFrom(start_date_orig, start_date) orElse
        renameFrom(end_date_orig, end_date) orElse
        renameFrom(created_date_orig, created_date) orElse
        renameFrom(last_modified_date_orig, last_modified_date) orElse
        renameFrom(put_code_orig, put_code)
    )

  }

  case class TranslatedTitle(value:String, language_code:String)
  object Renames_TranslatedTitle extends Serializable {
    val language_code = "language_code"
    val language_code_orig = "language-code"

    val renames = FieldSerializer[TranslatedTitle](
      renameTo(language_code, language_code_orig),
      renameFrom(language_code_orig, language_code)
    )
  }

  case class WorkTitle(
                      title:StringInfo,
                      subtitle:StringInfo,
                      translated_title:TranslatedTitle
                      ) {
    override def toString:String = {
      var r = title.value
      if (subtitle != null && subtitle.value != null)
        r = s"$r|||${subtitle.value}"
      if (translated_title != null && translated_title.value != null)
        r = s"$r|||${translated_title.value}"
      r
    }
  }

  object Renames_WorkTitle extends Serializable {
    val translated_title = "translated_title"
    val translated_title_orig = "translated-title"

    val renames = FieldSerializer[WorkTitle](
      renameTo(translated_title, translated_title_orig),
      renameFrom(translated_title_orig, translated_title)
    )
  }

  case class WorkCitation(
                         _type:String,
                         citation:String
                         )

  object Renames_WorkCitation extends Serializable {
    val _type = "_type"
    val _type_orig = "work-citation-type"

    val renames = FieldSerializer[WorkCitation](
      renameTo(_type, _type_orig),
      renameFrom(_type_orig, _type)
    )
  }

  case class WorkExtId(id_type:String, id_value:StringInfo)
  object Renames_WorkExtId extends Serializable {
    val id_type = "id_type"
    val id_type_orig = "work-external-identifier-type"

    val id_value = "id_value"
    val id_value_orig = "work-external-identifier-id"

    val renames = FieldSerializer[WorkExtId](
      renameTo(id_type, id_type_orig) orElse
        renameTo(id_value, id_value_orig),
      renameFrom(id_type_orig, id_type) orElse
        renameFrom(id_value_orig, id_value)
    )
  }

  val EmptyMap = Map[String,String]()

  case class WorkExtIds(ext_ids:Array[WorkExtId], scope:String) {
    def hasExtIds:Boolean = ext_ids != null && ext_ids.length > 0
    def getExtIdMap:Map[String, String] =
    try {
      if (hasExtIds)
        ext_ids.flatMap { eid =>
          if (eid.id_type == null || eid.id_value == null || eid.id_value.value == null)
            None
          else
            Option(eid.id_type.trim.toLowerCase -> eid.id_value.value.trim)
        }.toMap
      else EmptyMap
    }
    catch {
      case t:Throwable => {
        println(t.getMessage)
        throw t
      }
    }
  }

  object Renames_WorkExtIds extends Serializable {
    val ext_ids = "ext_ids"
    val ext_ids_orig = "work-external-identifier"

    val renames = FieldSerializer[WorkExtIds](
      renameTo(ext_ids, ext_ids_orig),
      renameFrom(ext_ids_orig, ext_ids)
    )
  }

  case class ContributorAttr(seq:String, role:String)

  object Renames_ContributorAttr extends Serializable {
    val seq = "seq"
    val seq_orig = "contributor-sequence"

    val role = "role"
    val role_orig = "contributor-role"

    val renames = FieldSerializer[ContributorAttr](
      renameTo(seq, seq_orig) orElse
        renameTo(role, role_orig),
      renameFrom(seq_orig, seq) orElse
        renameFrom(role_orig, role)
    )
  }

  case class WorkContributor(
                            orcid:OrcidIdentifier,
                            credit_name:NameInfo,
                            email:String,
                            attr:ContributorAttr
                            )

  object Renames_WorkContributor extends Serializable {
    val orcid = "orcid"
    val orcid_orig = "contributor-orcid"

    val credit_name = "credit_name"
    val credit_name_orig = "credit-name"

    val email = "email"
    val email_orig = "contributor-email"

    val attr = "attr"
    val attr_orig = "contributor-attributes"

    val renames = FieldSerializer[WorkContributor](
      renameTo(orcid, orcid_orig) orElse
        renameTo(credit_name, credit_name_orig) orElse
        renameTo(email, email_orig) orElse
        renameTo(attr, attr_orig),
      renameFrom(orcid_orig, orcid) orElse
        renameFrom(credit_name_orig, credit_name) orElse
        renameFrom(email_orig, email) orElse
        renameFrom(attr_orig, attr)
    )
  }

  case class WorkContributors(
                              contributor:Array[WorkContributor]
                            )

  case class OrcidWork(
                      put_code:String,
                      work_title:WorkTitle,
                      journal_title:StringInfo,
                      short_description:String,
                      work_citation:WorkCitation,
                      work_type:String,
                      publication_date:DateYMD_Media,
                      work_ext_ids:WorkExtIds,
                      url:StringInfo,
                      contributors:WorkContributors,
                      work_source:String,
                      source:OrcidSource,
                      created_date:DateInfo,
                      last_modified_date:DateInfo,
                      language_code:String,
                      country:ContactAddrCountry,
                      visibility:String
                      )
  {
    def hasExtIds:Boolean = work_ext_ids != null && work_ext_ids.hasExtIds
  }

  object Renames_OrcidWork extends Serializable {
    val put_code = "put_code"
    val put_code_orig = "put-code"

    val work_title = "work_title"
    val work_title_orig = "work-title"

    val journal_title = "journal_title"
    val journal_title_orig = "journal-title"

    val short_description = "short_description"
    val short_description_orig = "short-description"

    val work_citation = "work_citation"
    val work_citation_orig = "work-citation"

    val work_type = "work_type"
    val work_type_orig = "work-type"

    val publication_date = "publication_date"
    val publication_date_orig = "publication-date"

    val work_ext_ids = "work_ext_ids"
    val work_ext_ids_orig = "work-external-identifiers"

    val contributors = "contributors"
    val contributors_orig = "work-contributors"

    val work_source = "work_source"
    val work_source_orig = "work-source"

    val created_date = "created_date"
    val created_date_orig = "created-date"

    val last_modified_date = "last_modified_date"
    val last_modified_date_orig = "last-modified-date"

    val language_code = "language_code"
    val language_code_orig = "language-code"

    val renames = FieldSerializer[OrcidWork](
      renameTo(put_code, put_code_orig) orElse
        renameTo(work_title, work_title_orig) orElse
        renameTo(journal_title, journal_title_orig) orElse
        renameTo(short_description, short_description_orig) orElse
        renameTo(work_citation, work_citation_orig) orElse
        renameTo(work_type, work_type_orig) orElse
        renameTo(publication_date, publication_date_orig) orElse
        renameTo(work_ext_ids, work_ext_ids_orig) orElse
        renameTo(contributors, contributors_orig) orElse
        renameTo(work_source, work_source_orig) orElse
        renameTo(created_date, created_date_orig) orElse
        renameTo(last_modified_date, last_modified_date_orig) orElse
        renameTo(language_code, language_code_orig),
      renameFrom(put_code_orig, put_code) orElse
        renameFrom(work_title_orig, work_title) orElse
        renameFrom(journal_title_orig, journal_title) orElse
        renameFrom(short_description_orig, short_description) orElse
        renameFrom(work_citation_orig, work_citation) orElse
        renameFrom(work_type_orig, work_type) orElse
        renameFrom(publication_date_orig, publication_date) orElse
        renameFrom(work_ext_ids_orig, work_ext_ids) orElse
        renameFrom(contributors_orig, contributors) orElse
        renameFrom(work_source_orig, work_source) orElse
        renameFrom(created_date_orig, created_date) orElse
        renameFrom(last_modified_date_orig, last_modified_date) orElse
        renameFrom(language_code_orig, language_code)
    )

  }

  case class OrcidWorks(work:Array[OrcidWork], scope:String)

  object Renames_OrcidWorks extends Serializable {
    val work = "work"
    val work_orig = "orcid-work"

    val renames = FieldSerializer[OrcidWorks](
      renameTo(work, work_orig),
      renameFrom(work_orig, work)
    )
  }

  case class Affiliations(affiliation:Array[Affiliation])

  case class FundingTitle(
                         title:StringInfo,
                         translated_title: TranslatedTitle
                         )

  object Renames_FundingTitle extends Serializable {
    val translated_title = "translated_title"
    val translated_title_orig = "translated-title"

    val renames = FieldSerializer[FundingTitle](
      renameTo(translated_title, translated_title_orig),
      renameFrom(translated_title_orig, translated_title)
    )
  }

  case class AmountCurrency(value:String, currency:String)

  object Renames_AmountCurrency extends Serializable {
    val currency = "currency"
    val currency_orig = "currency-code"

    val renames = FieldSerializer[AmountCurrency](
      renameTo(currency, currency_orig),
      renameFrom(currency_orig, currency)
    )
  }

  case class FundingContributorAttrs(role:String)

  case class FundingContributor(
                               orcid:String,
                               credit_name:String,
                               email:String,
                               attrs:FundingContributorAttrs
                               )
  case class FundingEid(
                       _type:String,
                       value:String,
                       url:String
                       )
  case class FundingEids(eids:Array[FundingEid])

  case class FundingContributors(contributors:Array[FundingContributor])
  case class Funding(
                    put_code:String,
                    _type:String,
                    org_def_type:StringInfo,
                    title:FundingTitle,
                    short_desc:String,
                    amount:AmountCurrency,
                    url:StringInfo,
                    start_date:DateYMD,
                    end_date:DateYMD,
                    eids:FundingEids,
                    contributors:FundingContributors,
                    organization:Organization,
                    source:OrcidSource,
                    created_date:DateInfo,
                    last_modified_date:DateInfo,
                    visibility:String
                    )

  object Renames_Funding extends Serializable {
    val put_code = "put_code"
    val put_code_orig = "put-code"

    val _type = "_type"
    val _type_orig = "funding-type"

    val org_def_type = "org_def_type"
    val org_def_type_orig = "organization-defined-type"

    val title = "title"
    val title_orig = "funding-title"

    val short_desc = "short_desc"
    val short_desc_orig = "short-description"

    val start_date = "start_date"
    val start_date_orig = "start-date"

    val end_date = "end_date"
    val end_date_orig = "end-date"

    val eids = "eids"
    val eids_orig = "funding-external-identifiers"

    val contributors = "contributors"
    val contributors_orig = "funding-contributors"

    val created_date = "created_date"
    val created_date_orig = "created-date"

    val last_modified_date = "last_modified_date"
    val last_modified_date_orig = "last-modified-date"

    val renames = FieldSerializer[Funding](
      renameTo(put_code, put_code_orig)
        .orElse(renameTo(_type, _type_orig))
        .orElse(renameTo(org_def_type, org_def_type_orig))
        .orElse(renameTo(title, title_orig))
        .orElse(renameTo(short_desc, short_desc_orig))
        .orElse(renameTo(start_date, start_date_orig))
        .orElse(renameTo(end_date, end_date_orig))
        .orElse(renameTo(eids, eids_orig))
        .orElse(renameTo(contributors, contributors_orig))
        .orElse(renameTo(created_date, created_date_orig))
        .orElse(renameTo(last_modified_date, last_modified_date_orig)),
      renameFrom(put_code_orig, put_code)
        .orElse(renameFrom(_type_orig, _type))
        .orElse(renameFrom(org_def_type_orig, org_def_type))
        .orElse(renameFrom(title_orig, title))
        .orElse(renameFrom(short_desc_orig, short_desc))
        .orElse(renameFrom(start_date_orig, start_date))
        .orElse(renameFrom(end_date_orig, end_date))
        .orElse(renameFrom(eids_orig, eids))
        .orElse(renameFrom(contributors_orig, contributors))
        .orElse(renameFrom(created_date_orig, created_date))
        .orElse(renameFrom(last_modified_date_orig, last_modified_date))
    )
  }

  case class FundingList(funding:Array[Funding], scope:String)

  case class OrcidActivities(
    affiliations:Affiliations,
    works:OrcidWorks,
    funding_list:FundingList
  ) {
    def hasWorks:Boolean = works != null && works.work != null && works.work.length > 0
  }

  object Renames_OrcidActivities extends Serializable {

    val works = "works"
    val works_orig = "orcid-works"

    val funding_list = "funding_list"
    val funding_list_orig = "funding-list"

    val renames = FieldSerializer[OrcidActivities](
      renameTo(works, works_orig) orElse
        renameTo(funding_list, funding_list_orig),
      renameFrom(works_orig, works) orElse
        renameFrom(funding_list_orig, funding_list)
    )
  }

  private def countActivityEids(acts:Array[OrcidWork]):Map[String,Int] = {
    import collection.mutable

    val r = mutable.Map[String,Int]()

    acts.foreach { act =>
      if (act.work_ext_ids != null && act.work_ext_ids.ext_ids.nonEmpty) {
        act.work_ext_ids.ext_ids.foreach { eid =>
          if (!r.contains(eid.id_type)) r += eid.id_type -> 0
          r(eid.id_type) = r(eid.id_type)+1
        }
      }
    }
    r.toMap
  }


  case class Profile2015(
    orcid:String,
    orcid_id:String,
    orcid_identifier:OrcidIdentifier,
    orcid_deprecated:String,
    orcid_preferences:OrcidPreferences,
    history:OrcidHistory,
    bio:Bio,
    activities:OrcidActivities,
    orcid_internal:String,
    _type:String,
    group_type:String,
    client_type:String
  ) {

    def hasAffInfo:Boolean =
      activities != null && activities.affiliations != null && activities.affiliations.affiliation.length > 0

    def activityCount:Int = {
      if (activities != null && activities.works != null && activities.works.work != null)
        activities.works.work.length
      else 0
    }
    def hasActivities:Boolean = activityCount > 0

    def name:Option[String] =
      if (bio != null && bio.person_details != null) {
        val fn = bio.person_details.family_name
        val gn = bio.person_details.given_names
        if (fn != null && gn != null)
          Option(s"${gn.value} ${fn.value}")
        else None
      }
      else None

    def affInfoTrace:String =
      if (hasAffInfo) {
        val count = activities.affiliations.affiliation.length
        val currentAffs = activities.affiliations.affiliation.filter(_.end_date == null)

        val affInfo = currentAffs.map(_.organization.name).mkString("[", "],[", "]")
        s"$affInfo (total: $count)"
      }
      else "[Empty]"

    def actInfoTrace:String =
      if (hasActivities) {
        val count = activities.works.work.length
        val eidMap = countActivityEids(activities.works.work)
        val eidCounts = eidMap.toList.sortBy(_._1).map(p => s"${p._1}:${p._2}").mkString("[", ",", "]")
        s"$eidCounts (total: $count)"
      }
      else "[Empty]"

  }

  case class ActEid(t:String, v:String)
  case class ActivityExt(title:String, eids:Array[ActEid])

  case class ProfileExt(
                       orcid:String,
                       name:String,
                       aff:String,
                       actExtracts:Array[ActivityExt]
                       )

  def profileExt2Json(p:ProfileExt):String = {
    import org.json4s.jackson.Serialization._
    implicit val fmt = DefaultFormats
    write(p)
  }

  object Renames_Profile2015 extends Serializable {
    val orcid_id = "orcid_id"
    val orcid_id_orig = "orcid-id"

    val orcid_identifier = "orcid_identifier"
    val orcid_identifier_orig = "orcid-identifier"

    val orcid_deprecated = "orcid_deprecated"
    val orcid_deprecated_orig = "orcid-deprecated"

    val orcid_preferences = "orcid_preferences"
    val orcid_preferences_orig = "orcid-preferences"

    val history = "history"
    val history_orig = "orcid-history"

    val bio = "bio"
    val bio_orig = "orcid-bio"

    val activities = "activities"
    val activities_orig = "orcid-activities"

    val orcid_internal = "orcid_internal"
    val orcid_internal_orig = "orcid-internal"

    val _type = "_type"
    val _type_orig = "type"

    val group_type = "group_type"
    val group_type_orig = "group-type"

    val client_type = "client_type"
    val client_type_orig = "client-type"

    val renames = FieldSerializer[Profile2015](
      renameTo(orcid_id, orcid_id_orig) orElse
        renameTo(orcid_identifier, orcid_identifier_orig) orElse
        renameTo(orcid_preferences, orcid_preferences_orig) orElse
        renameTo(orcid_deprecated, orcid_deprecated_orig) orElse
        renameTo(history, history_orig) orElse
        renameTo(bio, bio_orig) orElse
        renameTo(activities, activities_orig) orElse
        renameTo(orcid_internal, orcid_internal_orig) orElse
        renameTo(_type, _type_orig) orElse
        renameTo(group_type, group_type_orig) orElse
        renameTo(client_type, client_type_orig),
      renameFrom(orcid_id_orig, orcid_id) orElse
        renameFrom(orcid_identifier_orig, orcid_identifier) orElse
        renameFrom(orcid_preferences_orig, orcid_preferences) orElse
        renameFrom(orcid_deprecated_orig, orcid_deprecated) orElse
        renameFrom(history_orig, history) orElse
        renameFrom(bio_orig, bio) orElse
        renameFrom(activities_orig, activities) orElse
        renameFrom(orcid_internal_orig, orcid_internal) orElse
        renameFrom(_type_orig, _type) orElse
        renameFrom(group_type_orig, group_type) orElse
        renameFrom(client_type_orig, client_type)
    )
  }

  case class OrcidProfile2015(
    message_version:String,
    profile:Profile2015,
    search_results:String,
    error_desc:StringInfo
  ) {
  }

  case class OrcidRecord(
                        orcid:StringInfo,
                        orcid_id:String,
                        orcid_identifier:String
                        )

  object Renames_OrcidRecord extends Serializable {
    val orcid_id = "orcid_id"
    val orcid_id_orig = "orcid-id"

    val orcid_identifier = "orcid_identifier"
    val orcid_identifier_orig = "orcid-identifier"

    val renames = FieldSerializer[OrcidRecord](
      renameTo(orcid_id, orcid_id_orig) orElse
        renameTo(orcid_identifier, orcid_identifier_orig),
      renameFrom(orcid_id_orig, orcid_id) orElse
        renameFrom(orcid_identifier_orig, orcid_identifier)
    )

  }

  case class DeprecatedProfile(
    deprecated_date:DateInfo,
    primary_record:OrcidRecord
  )

  object Renames_DeprecatedProfile extends Serializable {

    val deprecated_date = "deprecated_date"
    val deprecated_date_orig = "deprecated-date"

    val primary_record = "primary_record"
    val primary_record_orig = "primary-record"

    val renames = FieldSerializer[DeprecatedProfile](
      renameTo(deprecated_date, deprecated_date_orig) orElse
        renameTo(primary_record, primary_record_orig),
      renameFrom(deprecated_date_orig, deprecated_date) orElse
        renameFrom(primary_record_orig, primary_record)
    )

  }
  object Renames_OrcidProfile2015 extends Serializable {
    val message_version = "message_version"
    val message_version_orig = "message-version"

    val profile = "profile"
    val profile_orig = "orcid-profile"

    val search_results = "search_results"
    val search_results_orig = "orcid-search-results"

    val error_desc = "error_desc"
    val error_desc_orig = "error-desc"

    val deprecated_date = "deprecated_date"
    val deprecated_date_orig = "deprecated-date"

    val renames = FieldSerializer[OrcidProfile2015](
      renameTo(message_version, message_version_orig) orElse
        renameTo(profile, profile_orig) orElse
        renameTo(search_results, search_results_orig) orElse
        renameTo(error_desc, error_desc_orig),
      renameFrom(message_version_orig, message_version) orElse
        renameFrom(profile_orig, profile) orElse
        renameFrom(search_results_orig, search_results) orElse
        renameFrom(error_desc_orig, error_desc)
    )
  }

  private val _fmt = DefaultFormats +
    Renames_OrcidProfile2015.renames +
    Renames_Profile2015.renames +
    Renames_OrcidHistory.renames +
    Renames_OrcidSource.renames +
    Renames_Bio.renames +
    Renames_PersonalDetails.renames +
    Renames_OrcidActivities.renames +
    Renames_Affiliation.renames +
    Renames_Organization.renames +
    Renames_DisambiguatedOrganization.renames +
    Renames_OrcidWorks.renames +
    Renames_OrcidWork.renames +
    Renames_WorkTitle.renames +
    Renames_WorkCitation.renames +
    Renames_DateYMD_Media.renames +
    Renames_WorkContributor.renames +
    Renames_ContributorAttr.renames +
    Renames_WorkExtIds.renames +
    Renames_WorkExtId.renames +
    Renames_OtherNames.renames +
    Renames_ExternalIdentifier.renames +
    Renames_ExternalIdentifiers.renames +
    Renames_ResearcherUrls.renames +
    Renames_ResearcherUrl.renames +
    Renames_TranslatedTitle.renames +
    Renames_Funding.renames +
    Renames_FundingTitle.renames +
    Renames_AmountCurrency.renames +
    Renames_ContactEmail.renames

  private val _fmt_dep = DefaultFormats +
    Renames_DeprecatedProfile.renames +
    Renames_OrcidRecord.renames

  def readJson(j:String):OrcidProfile2015 = {
    import org.json4s.jackson.JsonMethods._
    implicit val fmt = _fmt
    parse(j).extract[OrcidProfile2015]
  }

  def readDepRecJson(j:String):DeprecatedProfile = {
    import org.json4s.jackson.JsonMethods._
    implicit val fmt = _fmt_dep
    parse(j).extract[DeprecatedProfile]
  }
}
