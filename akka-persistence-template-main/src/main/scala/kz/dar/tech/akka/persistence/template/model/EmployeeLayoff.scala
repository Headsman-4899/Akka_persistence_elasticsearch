package kz.dar.tech.akka.persistence.template.model

import org.joda.time.DateTime

case class EmployeeLayoff(employeeId: String,
                          isLayoffs: Boolean)