using StudentsAuthorize.Models;
using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web;

namespace StudentsAuthorize.BLL
{
    public class StudentsBLL
    {
        public IEnumerable<Student> GetStudents()
        {
            using (StudentsDBEntities entities = new StudentsDBEntities())
            {
                return entities.Students.ToList();
            }
        }

        public IEnumerable<Student> GetStudentsWithAge(int age, string criteria)
        {
            using (StudentsDBEntities entities = new StudentsDBEntities())
            {
                switch (criteria)
                {
                    case "eq":
                        return entities.Students.Where(s => s.Age == age).ToList();
                    case "gt":
                        return entities.Students.Where(s => s.Age > age).ToList();
                    default /*case "lt"*/:
                        return entities.Students.Where(s => s.Age < age).ToList();
                }
            }
        }

        public IEnumerable<Student> GetStudentsWithGrade(int grade)
        {
            using (StudentsDBEntities entities = new StudentsDBEntities())
            {
                return entities.Students.Where(s => s.Grade == grade).ToList();
            }
        }

        public Student GetStudent(int id)
        {
            using (StudentsDBEntities entities = new StudentsDBEntities())
            {
                return entities.Students.FirstOrDefault(s => s.ID == id);
            }
        }

        public HttpResponseMessage CreateStudent(HttpRequestMessage Request, Student student)
        {
            try
            {
                using (StudentsDBEntities entities = new StudentsDBEntities())
                {
                    entities.Students.Add(student);
                    entities.SaveChanges();

                    var message = Request.CreateResponse(HttpStatusCode.Created, student);
                    message.Headers.Location = new Uri(Request.RequestUri + "/" + student.ID.ToString());
                    return message;
                }
            }
            catch (Exception ex)
            {
                return Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex);
            }
        }

        public HttpResponseMessage UpdateStudent(HttpRequestMessage Request, int id, Student student)
        {
            try
            {
                using (StudentsDBEntities entities = new StudentsDBEntities())
                {
                    var entity = entities.Students.FirstOrDefault(s => s.ID == id);

                    if (entity == null)
                    {
                        return Request.CreateErrorResponse(HttpStatusCode.NotFound, "No student found with id " + id);
                    }
                    else
                    {
                        entity.FirstName = student.FirstName;
                        entity.Age = student.Age;
                        entity.Grade = student.Grade;
                        entity.ImageUrl = student.ImageUrl;

                        entities.SaveChanges();

                        return Request.CreateResponse(HttpStatusCode.OK, entity);
                    }
                }
            }
            catch (Exception ex)
            {
                return Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex);
            }
        }

        public HttpResponseMessage DeleteStudent(HttpRequestMessage Request, int id)
        {
            try
            {
                using (StudentsDBEntities entities = new StudentsDBEntities())
                {
                    var entity = entities.Students.FirstOrDefault(s => s.ID == id);
                    if (entity == null)
                    {
                        return Request.CreateErrorResponse(HttpStatusCode.NotFound, "No student found with id " + id);
                    }
                    else
                    {
                        entities.Students.Remove(entity);
                        entities.SaveChanges();
                        return Request.CreateResponse(HttpStatusCode.OK);
                    }
                }
            }
            catch (Exception ex)
            {
                return Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex);
            }
        }

        public HttpResponseMessage DeleteAllStudents(HttpRequestMessage Request)
        {
            try
            {
                using (StudentsDBEntities entities = new StudentsDBEntities())
                {
                    entities.Students.RemoveRange(entities.Students);
                    entities.SaveChanges();

                    return Request.CreateResponse(HttpStatusCode.OK);
                }
            }
            catch (Exception ex)
            {
                return Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex);
            }
        }

        public HttpResponseMessage DeleteStudents(HttpRequestMessage Request, StudentIds studentIds)
        {
            try
            {
                using (StudentsDBEntities entities = new StudentsDBEntities())
                {
                    foreach(int id in studentIds.ids)
                    {
                        var entity = entities.Students.FirstOrDefault(s => s.ID == id);
                        if (entity == null)
                        {
                            return Request.CreateErrorResponse(HttpStatusCode.NotFound, "No student found with id " + id);
                        }
                    }

                    foreach(int id in studentIds.ids)
                    {
                        var entity = entities.Students.FirstOrDefault(s => s.ID == id);
                        entities.Students.Remove(entity);
                    }

                    entities.SaveChanges();
                    return Request.CreateResponse(HttpStatusCode.OK);
                }
            }
            catch (Exception ex)
            {
                return Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex);
            }
        }

        public HttpResponseMessage IsAdmin(HttpRequestMessage Request, String username)
        {
            try
            {
                using (ProfilesDBEntities entities = new ProfilesDBEntities())
                {
                    var entity = entities.Profiles.FirstOrDefault(p => p.UserName == username);
                    if (entity == null)
                    {
                        return Request.CreateErrorResponse(HttpStatusCode.NotFound, "No user found with name " + username);
                    }
                    else
                    {
                        return Request.CreateResponse(HttpStatusCode.OK, entity);
                    }
                }
            }
            catch (Exception ex)
            {
                return Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex);
            }
        }
    }
}