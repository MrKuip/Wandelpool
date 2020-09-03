package org.kku.wandelpool.domain;


public class BulletinBoardItem
{
  private String datum;
  private String auteur;
  private String text;

  public String getDatum()
  {
    return datum;
  }

  public void setDate(
      String datum)
  {
    this.datum = datum;
  }

  public String getAuteur()
  {
    return auteur;
  }

  public void setAuthor(
      String auteur)
  {
    this.auteur = auteur;
  }

  public String getText()
  {
    return text;
  }

  public void setText(
      String text)
  {
    this.text = text;
  }

}
